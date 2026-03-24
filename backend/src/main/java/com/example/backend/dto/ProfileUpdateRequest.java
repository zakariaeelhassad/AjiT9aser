package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    private String username;
    private String email;
    private String teamName;
    private String currentPassword;
    private String newPassword;
    private String profileImage;
    private String teamImage;
}
