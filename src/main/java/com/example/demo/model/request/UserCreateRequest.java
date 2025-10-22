package com.example.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for creating a new user")
public class UserCreateRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email max length is 255")
    private String email;

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 255, message = "Username max length is 255")
    private String username;

    @Size(max = 1024, message = "Avatar URL max length is 1024")
    private String avatarUrl;

    @NotBlank(message = "Provider cannot be blank")
    @Size(max = 50, message = "Provider max length is 50")
    private String provider;

    @NotBlank(message = "Provider ID cannot be blank")
    @Size(max = 255, message = "Provider ID max length is 255")
    private String providerId;
}