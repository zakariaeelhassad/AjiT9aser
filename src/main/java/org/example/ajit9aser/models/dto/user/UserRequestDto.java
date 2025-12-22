package org.example.ajit9aser.models.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(

        @NotBlank
        String username,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {}