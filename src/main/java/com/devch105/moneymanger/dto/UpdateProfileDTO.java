package com.devch105.moneymanger.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileDTO {
    private String fullName;
    @Email
    private String email;
    private String newPassword;
    private String newPasswordConfirm;
    private String oldPassword;
    private String profileImageUrl;
}
