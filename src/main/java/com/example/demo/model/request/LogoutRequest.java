package com.example.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "LogoutRequest", description = "Request object for user logout")
public class LogoutRequest {

    @NotBlank(
            message = "Token is required")
    @Schema(
            description = "The token to be invalidated during logout",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    String token;
}
