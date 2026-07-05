package com.devch105.moneymanger.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OTPGenerator {
    private static final SecureRandom random = new SecureRandom();

    public String generateOTP(){
        int otp= 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
