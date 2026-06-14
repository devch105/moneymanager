package com.devch105.moneymanger.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {


    private final JavaMailSender mailSender;



    @Value("${spring.mail.username}")
    private String username;
//    @Value("${spring.mail.password}")
//    private String password;

    @Value("${app.mail.from}")
    private String fromEmail;

    @PostConstruct
    public void test() {
        System.out.println("MAIL USERNAME = " + username);
        System.out.println("FROM EMAIL = " + fromEmail);
        // System.out.println("MAIL PASSWORD = " + password);
    }


    public void sendEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("SEND EMAIL TO EMAIL: " + to);
        }catch (Exception e){
            log.error("Full email error", e);
            throw e;
        }
    }
}