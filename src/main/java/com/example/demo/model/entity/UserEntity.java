package com.example.demo.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Entity đại diện cho người dùng trong hệ thống.")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID duy nhất của người dùng.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Địa chỉ email của người dùng.", example = "")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Tên hiển thị của người dùng.", example = "john_doe")
    private String username;

    @Column(name = "avatar_url", length = 1024)
    @Schema(description = "URL ảnh đại diện của người dùng.", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Column(nullable = false, length = 50)
    @Schema(description = "Nhà cung cấp dịch vụ xác thực (ví dụ: google, facebook).", example = "google")
    private String provider;

    @Column(name = "provider_id", nullable = false)
    @Schema(description = "ID người dùng do nhà cung cấp dịch vụ xác thực cung cấp.", example = "1234567890")
    private String providerId;
}