package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.AuthDTO;
import com.devch105.moneymanger.dto.PendingProfileUpdateDTO;
import com.devch105.moneymanger.dto.ProfileDTO;
import com.devch105.moneymanger.dto.UpdateProfileDTO;
import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.ProfileRepository;
import com.devch105.moneymanger.util.JWTUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private  final EmailService emailService;
    private  final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final OtpService otpService;

    @Value("${money.manager.activation.url}")
    private  String activationUrl;


   public boolean isAccountActive(String email){
       return profileRepository.findByEmail(email).map(ProfileEntity::getIsActive).orElse(false);
   }

   public ProfileEntity getCurrentProfile(){
       Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
       ProfileEntity entity = profileRepository.findByEmail(authenticatedUser.getName()).orElseThrow(()-> new UsernameNotFoundException("User not found in database : "+authenticatedUser.getName()));
       log.info("Fetching Current Profile : "+entity);
       return  entity;

   }

   public HashMap<String,String> updateProfile(UpdateProfileDTO profileDTO){
     try{
         HashMap<String, String> map = new HashMap<>();
         map.put("status","success");

         ProfileEntity currentUser = getCurrentProfile();

         authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(
                         currentUser.getEmail(),
                         profileDTO.getOldPassword()
                 )
         );

        boolean emailChanged = !currentUser.getEmail().equals(profileDTO.getEmail());

        if(emailChanged){
            if(profileRepository.findByEmail(profileDTO.getEmail()).isPresent()){
                throw new UsernameNotFoundException("Email already in use");
            }
            PendingProfileUpdateDTO pendingProfileUpdateDTO = new PendingProfileUpdateDTO();
            pendingProfileUpdateDTO.setEmail(profileDTO.getEmail());
            pendingProfileUpdateDTO.setFullName(profileDTO.getFullName());
            pendingProfileUpdateDTO.setProfileImageUrl(profileDTO.getProfileImageUrl());
            pendingProfileUpdateDTO.setPassword(passwordEncoder.encode(profileDTO.getNewPassword()));
            String json = objectMapper.writeValueAsString(pendingProfileUpdateDTO);
            redisTemplate.opsForValue().set("pending-update:"+currentUser.getId(),json, Duration.ofMinutes(5));
            otpService.generateOtpAndMail(currentUser.getEmail());

            map.put("status","pending-update");
            return map;
        }
//         ProfileEntity currentUser = getCurrentProfile();
         currentUser.setFullName(profileDTO.getFullName());
         currentUser.setProfileImageUrl(profileDTO.getProfileImageUrl());
         currentUser.setPassword(passwordEncoder.encode(profileDTO.getNewPassword()));
         profileRepository.save(currentUser);
        return  map;
     }catch(Exception e){
         log.error("Profile Update Failed : "+e);
         throw new UsernameNotFoundException("User not found in database : "+profileDTO.getEmail());

     }

   }



    public HashMap<String, Object> updateAfterVerfication(String otp) throws JsonProcessingException {

        ProfileEntity currentProfile = getCurrentProfile();

        HashMap<String, Object> map = new HashMap<>();

        String json = redisTemplate.opsForValue()
                .get("pending-update:" + currentProfile.getId());

        if (json == null) {
            throw new RuntimeException("Pending profile update not found.");
        }

        PendingProfileUpdateDTO pending =
                objectMapper.readValue(json, PendingProfileUpdateDTO.class);

        // Verify OTP sent to NEW email
        otpService.verifyOtp(currentProfile.getEmail(), otp);

        // Update profile
        currentProfile.setProfileImageUrl(pending.getProfileImageUrl());
        currentProfile.setFullName(pending.getFullName());
        currentProfile.setEmail(pending.getEmail());
        currentProfile.setPassword(pending.getPassword()); // Already encoded

        profileRepository.save(currentProfile);
        ProfileDTO dto = toDTO(currentProfile);
        // Cleanup
        redisTemplate.delete("pending-update:" + currentProfile.getId());

        // Generate NEW JWT
        String token = jwtUtil.generateToken(currentProfile.getEmail());

        map.put("status", "success");
        map.put("message", "Profile updated successfully.");
        map.put("token", token);
        map.put("user", dto );

        return map;
    }

















   public ProfileDTO getPublicProfile(String email){
       ProfileEntity user = null;
       if(email == null || email.isEmpty()){
           user = getCurrentProfile();
       }else{
           log.info("Fetching Public Profile : "+user);
           user = profileRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found in database : "+email));
       }
       return toDTO(user);
   }
   @Transactional
   public ProfileDTO registerProfile(ProfileDTO profileDTO){
       if(profileRepository
               .findByEmail(profileDTO.getEmail())
               .isPresent()){

           throw new RuntimeException(
                   "Email already registered"
           );
       }
       ProfileEntity newProfile = toEntity(profileDTO);
       newProfile.setActivationToken(UUID.randomUUID().toString());
       newProfile = profileRepository.save(newProfile);
       // send activation link
      sendActivationEmail(newProfile);

       return toDTO(newProfile);
   }
    private void sendActivationEmail(ProfileEntity profile) {

        String activationLink =
                activationUrl + "/activate?token=" + profile.getActivationToken();

        String subject = "Activate your Money Manager Account";

        String emailBody = """
        <div style="font-family:Arial,sans-serif">

            <h2>Welcome to Money Manager</h2>

            <p>Thank you for registering.</p>

            <p>Click the button below to activate your account:</p>

            <a href="%s"
               style="
                    background:#2563eb;
                    color:white;
                    padding:12px 20px;
                    text-decoration:none;
                    border-radius:6px;
               ">
                Activate Account
            </a>

            <p>If the button doesn't work, copy this link:</p>

            <p>%s</p>

        </div>
        """.formatted(activationLink, activationLink);

        try {

            emailService.sendEmail(
                    profile.getEmail(),
                    subject,
                    emailBody
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to send email: "
                            + e.getMessage()
            );

        }
    }
    @Transactional
    public void resendActivationEmail(String email) {
       ProfileEntity profile = profileRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
       if(profile.getIsActive()){
           throw new RuntimeException("User is already active.");
       }
       profile.setActivationToken(UUID.randomUUID().toString());
       profileRepository.save(profile);
       sendActivationEmail(profile);
    }
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {

                    profileEntity.setIsActive(true);

                    // prevent reuse
                    profileEntity.setActivationToken(null);

                    profileRepository.save(profileEntity);

                    return true;
                }).orElse(false);
    }
    public boolean isEmailExists(String email){
        return profileRepository.findByEmail(email).isPresent();
    }
    // DTO to Entity
    public ProfileEntity toEntity(ProfileDTO profileDTO){
       return ProfileEntity.builder()
               .id(profileDTO.getId())
               .fullName(profileDTO.getFullName())
               .email(profileDTO.getEmail())
               .password(passwordEncoder.encode(profileDTO.getPassword()))
               .profileImageUrl(profileDTO.getProfileImageUrl())
               .createdAt(profileDTO.getCreatedAt())
               .updatedAt(profileDTO.getUpdatedAt())
               .build();
    }

    // Entity to DTO
    public ProfileDTO toDTO(ProfileEntity profileEntity){
       return ProfileDTO.builder()
               .id(profileEntity.getId())
               .fullName(profileEntity.getFullName())
               .email(profileEntity.getEmail())
               .profileImageUrl(profileEntity.getProfileImageUrl())
               .profileImageUrl(profileEntity.getProfileImageUrl())
               .createdAt(profileEntity.getCreatedAt())
               .updatedAt(profileEntity.getUpdatedAt())
               .build();
    }


    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
       try {
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
          // JWT create token
           String token = jwtUtil.generateToken(authDTO.getEmail());
           return Map.of("token",token,
           "User",getPublicProfile(authDTO.getEmail()));
       }catch (Exception e){
           throw new UsernameNotFoundException("Invalid username and password");
       }
   }
}
