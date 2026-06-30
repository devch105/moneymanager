package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.dto.AuthDTO;
import com.devch105.moneymanger.dto.EmailRequestDTO;
import com.devch105.moneymanger.dto.ProfileDTO;
import com.devch105.moneymanger.dto.ResendEmailRequestDTO;
import com.devch105.moneymanger.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;


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
