package com.devch105.moneymanger.service;

import com.devch105.moneymanger.util.OTPGenerator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final EmailService emailService;
    private final OTPGenerator  otpGenerator;
    private final StringRedisTemplate redisTemplate;


    public void generateOtpAndMail(String email){
        String otp = otpGenerator.generateOTP();
        String key = "otp:"+email;


        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new RuntimeException("OTP already sent. Please wait before requesting a new one.");
        }else{
            redisTemplate.opsForValue().set(
                    key,
                    otp,
                    Duration.ofMinutes(5)
            );
            sendOTPEmail(email,otp);
        }
    }
    public void sendOTPEmail(String to, String otp) {

        String subject = "Verify Your Email - Money Manager";

        String body = """
        <div style="font-family: Arial, sans-serif; max-width:600px; margin:auto; border:1px solid #e5e7eb; border-radius:10px; overflow:hidden;">
            
            <div style="background:#203239; color:white; padding:20px; text-align:center;">
                <h2>Money Manager</h2>
            </div>

            <div style="padding:30px;">
                <h3>Hello,</h3>

                <p>
                    Use the following OTP to verify your email address.
                </p>

                <div style="
                    font-size:32px;
                    font-weight:bold;
                    letter-spacing:8px;
                    text-align:center;
                    margin:30px 0;
                    color:#203239;">
                    %s
                </div>

                <p>
                    This OTP will expire in
                    <strong>5 minutes</strong>.
                </p>

                <p style="color:#ef4444;">
                    Never share this OTP with anyone.
                </p>

                <hr>

                <p style="font-size:12px;color:#6b7280;">
                    If you didn't request this verification,
                    you can safely ignore this email.
                </p>
            </div>

        </div>
        """.formatted(otp);

       emailService.sendEmail(to, subject, body);
    }
    public boolean verifyOtp(String email, String otp) {
        String key = "otp:"+email;
        String storedOtp = redisTemplate.opsForValue().get(key);
        if (storedOtp == null) {
            throw new RuntimeException("OTP has expired");
        }
        if(!storedOtp.equals(otp)) {
            throw new RuntimeException("OTP does not match");
        }
        redisTemplate.delete(key);
        return true;
    }

}
