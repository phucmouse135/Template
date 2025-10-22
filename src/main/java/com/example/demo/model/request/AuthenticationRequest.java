package com.example.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "AuthenticationRequest", description = "Request object for user authentication")
public class AuthenticationRequest {

    @NotBlank(
            message = "Username is required")
    @Size(
            max = 50,
            message = "Username must not exceed 50 characters")
    @Schema(description = "Username of the user", example = "john_doe", maxLength = 50)
    String username;

    @NotBlank(
            message = "Password is required")
    @Size(
            max = 100,
            message = "Password must not exceed 100 characters"
            )
    @Schema(description = "Password of the user", example = "securePassword123", maxLength = 100)
    String password;
}
