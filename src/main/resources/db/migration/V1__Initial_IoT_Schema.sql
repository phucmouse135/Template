-- Bảng token hết hạn
CREATE TABLE IF NOT EXISTS invalidated_tokens (
                                                  id VARCHAR(255) PRIMARY KEY,
                                                  expiry_time TIMESTAMP NOT NULL,
                                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                  deleted_at TIMESTAMP NULL
);

-- Bảng role
CREATE TABLE IF NOT EXISTS roles (
                                     name VARCHAR(50) PRIMARY KEY,
                                     description VARCHAR(255),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     deleted_at TIMESTAMP NULL
);

-- Bảng user
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     username VARCHAR(255) NOT NULL,
                                     avatar_url VARCHAR(1024),
                                     provider VARCHAR(50) NOT NULL,
                                     provider_id VARCHAR(255) NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     deleted_at TIMESTAMP NULL,
                                     CONSTRAINT uk_provider_id UNIQUE (provider, provider_id)
);

-- Quan hệ user - role
CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id BIGINT NOT NULL,
                                           role_name VARCHAR(50) NOT NULL,
                                           PRIMARY KEY (user_id, role_name),
                                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                           FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE
);

-- Bảng thiết bị
CREATE TABLE IF NOT EXISTS devices (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT,
                                       device_uid VARCHAR(100) NOT NULL UNIQUE,
                                       name VARCHAR(255) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       deleted_at TIMESTAMP NULL,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Bảng telemetry
CREATE TABLE IF NOT EXISTS telemetry_logs (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              device_id BIGINT NOT NULL,
                                              log_time TIMESTAMP NOT NULL,
                                              temperature DECIMAL(5,2),
                                              air_humidity DECIMAL(5,2),
                                              light_level DECIMAL(10,2),
                                              soil_moisture DECIMAL(5,2),
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              deleted_at TIMESTAMP NULL,
                                              FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

-- Index tăng tốc truy vấn
CREATE INDEX idx_telemetry_device_time ON telemetry_logs(device_id, log_time DESC);
CREATE INDEX idx_device_uid ON devices(device_uid);
