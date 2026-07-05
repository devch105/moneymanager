package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.dto.*;
import com.devch105.moneymanger.service.OtpService;
import com.devch105.moneymanger.service.ProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        try{
            if(authDTO == null){
                return  ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message","Email and password are required"));
            }else{

                if(profileService.isAccountActive(authDTO.getEmail())){
                    Map<String , Object> response = profileService.authenticateAndGenerateToken(authDTO);
                    log.info("User Logged in Successfully Token Generated");
                    return ResponseEntity.ok(response);
                }else{
                    return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Please verify your email before logging in."));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid Email or Password"));
        }
    }


    @PostMapping("/update")
    public ResponseEntity<?> updateProfile( @RequestBody UpdateProfileDTO profileDTO) {
        HashMap<String , String> map =    profileService.updateProfile(profileDTO);
        if(map.get("status").equals("success")){
            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "message", "Profile updated successfully"
                    )
            );
        }else{
            return ResponseEntity.ok(Map.of("message", "Successfully send OTP to your mail, Please Verify !","status", "pending"));
        }
    }


//    @PostMapping("/send-otp")
//    public ResponseEntity<?> sendOTP(@Valid @RequestBody SendOtpDTO request){
//        otpService.generateOtpAndMail(request.getEmail());
//        return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent successfully"));
//    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody VerifyOtpDTO request) throws JsonProcessingException {
       HashMap<String,Object>  map =   profileService.updateAfterVerfication(request.getOtp());
        if(map.get("status").equals("success")){
            Object token = map.get("token");
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "OTP verified successfully",
                    "token", token,
                    "user", map.get("user")
            ));
        }else{
            return ResponseEntity.ok(Map.of( "status","failed","message", "OTP verification failed"));
        }
    }



    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile( @RequestBody  ProfileDTO profileDTO) {
        ProfileDTO newProfile = profileService.registerProfile(profileDTO);
        log.info("New Profile Created Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(newProfile);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestBody EmailRequestDTO requestDTO) {
        profileService.resendActivationEmail(requestDTO.getEmail());
        return ResponseEntity.ok(Map.of("message","Verification Email Sent successfully"));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated){
            log.info("Activated Profile Successfully");
            return ResponseEntity.status(HttpStatus.OK).body("Profile has been Activated");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Activation token not found or already used.");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        log.info("Test Profile Successfully");
        return ResponseEntity.status(HttpStatus.OK).body("Test Successful");
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile(){

        log.info("PROFILE ENDPOINT HIT");

        ProfileDTO dto = profileService.getPublicProfile(null);

        return ResponseEntity.ok(dto);
    }

}
