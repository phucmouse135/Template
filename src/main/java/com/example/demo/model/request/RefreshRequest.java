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
@Schema(name = "RefreshRequest", description = "Request object for refreshing a token")
public class RefreshRequest {

    @NotBlank(
            message = "Token is required")
    @Schema(
            description = "The token to be refreshed",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    String token;
}
