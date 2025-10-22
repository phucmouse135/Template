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
@Schema(name = "RoleRequest", description = "Request object for roles")
public class RoleRequest {

    @NotBlank(
            message = "Role name is required")
    @Size(
            max = 50,
            message = "Role name must not exceed 50 characters")
    @Schema(description = "Name of the role", example = "ADMIN", maxLength = 50, required = true)
    String name;

    @Size(
            max = 255,
            message = "Description must not exceed 255 characters")
    @Schema(description = "Description of the role", example = "Administrator role with full access", maxLength = 255)
    String description;
}
