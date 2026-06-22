package com.devch105.moneymanger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendEmailRequestDTO {
    private String from;
    private List<String> to;
    private String subject;
    private String html;

    private List<AttachmentDTO> attachments;
}
