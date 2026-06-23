package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.ResendEmailRequestDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import com.devch105.moneymanger.dto.AttachmentDTO;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final WebClient.Builder webClientBuilder;

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    @PostConstruct
    public void init() {
        System.out.println("Initializing email service!!");

        System.out.println("From email: " + fromEmail);
    }

    public void sendEmail(
            String to,
            String subject,
            String body
    ) {

        ResendEmailRequestDTO request =
                new ResendEmailRequestDTO();

        request.setFrom(fromEmail);
        request.setTo(List.of(to));
        request.setSubject(subject);
        request.setHtml(body);

        try {

            String response = webClientBuilder.build()
                    .post()
                    .uri("https://api.resend.com/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            clientResponse ->
                                    clientResponse.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("Resend Error Body: {}", errorBody);
                                                return reactor.core.publisher.Mono.error(
                                                        new RuntimeException(errorBody)
                                                );
                                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("Email sent successfully with Response  : {} : to User :  {} ", response, to);

        } catch (Exception e) {

            log.error("Failed to send email", e);
            throw e;
        }
    }


    public void sendEmailWithAttachment(
            String to,
            String subject,
            String body,
            byte[] attachment,
            String fileName
    ) {

        String base64File =
                Base64.getEncoder().encodeToString(attachment);

        AttachmentDTO attachmentDTO =
                new AttachmentDTO();

        attachmentDTO.setFilename(fileName);
        attachmentDTO.setContent(base64File);

        ResendEmailRequestDTO request =
                new ResendEmailRequestDTO();

        request.setFrom(fromEmail);
        request.setTo(List.of(to));
        request.setSubject(subject);
        request.setHtml(body);
        request.setAttachments(
                List.of(attachmentDTO)
        );

        try {

            String response = webClientBuilder.build()
                    .post()
                    .uri("https://api.resend.com/emails")
                    .header(
                            "Authorization",
                            "Bearer " + apiKey
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info(
                    "Email with attachment sent: {}",
                    response
            );

        } catch (Exception e) {

            log.error(
                    "Failed to send email with attachment",
                    e
            );

            throw e;
        }
    }
}