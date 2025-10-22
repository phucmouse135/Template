package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin người dùng (User DTO)")
public class UserDto {

    @Schema(description = "ID người dùng", example = "1")
    private Long id;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Size(max = 255)
    @Schema(description = "Địa chỉ email của người dùng", example = "john.doe@gmail.com", required = true)
    private String email;

    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(max = 255)
    @Schema(description = "Tên hiển thị của người dùng", example = "John Doe", required = true)
    private String username;

    @Size(max = 1024)
    @Schema(description = "Đường dẫn ảnh đại diện", example = "https://example.com/avatar/john.jpg")
    private String avatarUrl;

    @NotBlank(message = "Provider không được để trống")
    @Size(max = 50)
    @Schema(description = "Nhà cung cấp đăng nhập (google, github, local...)", example = "google", required = true)
    private String provider;

    @NotBlank(message = "Provider ID không được để trống")
    @Size(max = 255)
    @Schema(description = "ID người dùng từ provider (Google ID, Github ID...)", example = "google-109302189", required = true)
    private String providerId;

    @Schema(description = "Thời điểm tạo", example = "2025-10-22T08:00:00")
    private Instant createdAt;

    @Schema(description = "Thời điểm cập nhật", example = "2025-10-22T08:30:00")
    private Instant updatedAt;

    @Schema(description = "Thời điểm xóa mềm (nếu có)", example = "2025-10-25T00:00:00")
    private Instant deletedAt;

    @Schema(description = "Danh sách thiết bị thuộc người dùng")
    private List<DeviceDto> devices;
}
