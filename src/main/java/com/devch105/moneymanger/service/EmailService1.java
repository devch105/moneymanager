package com.devch105.moneymanger.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {


  //  private final JavaMailSender mailSender;

//    @Value("${spring.mail.username}")
//    private String username;
////    @Value("${spring.mail.password}")
////    private String password;
//
//    @Value("${app.mail.from}")
//    private String fromEmail;
//
//    @PostConstruct
//    public void test() {
//        System.out.println("MAIL USERNAME = " + username);
//        System.out.println("FROM EMAIL = " + fromEmail);
//       // System.out.println("MAIL PASSWORD = " + password);
//    }

@Value("${brevo.api.key}")
private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void test() {
        System.out.println("BREVO API KEY = " + apiKey);
        System.out.println("FROM EMAIL = " + fromEmail);
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", Map.of("email", fromEmail));
            requestBody.put("to", List.of(Map.of("email", to)));
            requestBody.put("subject", subject);
            requestBody.put("textContent", body);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("Email sent to: {} | Status: {}", to, response.getStatusCode());

        } catch (Exception e) {
            log.error("Full email error", e);
            throw e;
        }
    }

//    public void sendEmail(String to, String subject, String body) {
//        try{
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(body);
//            mailSender.send(message);
//            log.info("SEND EMAIL TO EMAIL: " + to);
//        }catch (Exception e){
//            log.error("Full email error", e);
//            throw e;
//        }
//    }
}
