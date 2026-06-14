package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.AuthDTO;
import com.devch105.moneymanger.dto.ProfileDTO;
import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.ProfileRepository;
import com.devch105.moneymanger.util.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ProfileService {

    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private  final EmailService emailService;
    private  final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;




   public boolean isAccountActive(String email){
       return profileRepository.findByEmail(email).map(ProfileEntity::getIsActive).orElse(false);
   }

   public ProfileEntity getCurrentProfile(){
       Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
      return  profileRepository.findByEmail(authenticatedUser.getName())
              .orElseThrow(()-> new UsernameNotFoundException("User not found in database : "+authenticatedUser.getName()));
   }

   public ProfileDTO getPublicProfile(String email){
       ProfileEntity user = null;
       if(email == null){
           user = getCurrentProfile();
       }else{
           user = profileRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found in database : "+email));
       }
       return toDTO(user);
   }

   public ProfileDTO registerProfile(ProfileDTO profileDTO){
       ProfileEntity newProfile = toEntity(profileDTO);
       newProfile.setActivationToken(UUID.randomUUID().toString());
       newProfile = profileRepository.save(newProfile);
       // send activation link
       String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
       String subject = "Activate your Money Manager Account";
       String emailBody = "Click on the following link to activate your Money Manager account : \n "+activationLink;

       emailService.sendEmail(newProfile.getEmail(), subject, emailBody);

       return toDTO(newProfile);
   }

   public boolean activateProfile(String activationToken){
       return profileRepository.findByActivationToken(activationToken)
               .map(profileEntity -> { profileEntity.setIsActive(true);
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
