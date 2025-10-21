-- Bảng người dùng (quản lý dashboard)
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       username VARCHAR(255) NOT NULL,
                       avatar_url VARCHAR(1024),
                       provider VARCHAR(50) NOT NULL,
                       provider_id VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT uk_provider_id UNIQUE (provider, provider_id)
);

-- Bảng quản lý các thiết bị IoT
CREATE TABLE devices (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT, -- Người sở hữu thiết bị này
                         device_uid VARCHAR(100) NOT NULL UNIQUE, -- ID mà ESP32 tự khai báo (ví dụ: ESP32_GARDEN_01)
                         name VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Bảng lưu trữ lịch sử (Bảng này sẽ rất LỚN)
CREATE TABLE telemetry_logs (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                device_id BIGINT NOT NULL,
                                log_time TIMESTAMP NOT NULL,
                                temperature DECIMAL(5, 2),
                                air_humidity DECIMAL(5, 2),
                                light_level DECIMAL(10, 2),
                                soil_moisture DECIMAL(5, 2),
                                FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

-- Tối ưu hiệu năng truy vấn lịch sử
CREATE INDEX idx_telemetry_device_time ON telemetry_logs(device_id, log_time DESC);
CREATE INDEX idx_device_uid ON devices(device_uid);
