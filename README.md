Đây là một file README.md chi tiết, mô tả toàn bộ dự án IoT Vườn Thông Minh, bao gồm kiến trúc, công nghệ, và hướng dẫn chạy thử.

-----

# Nền tảng IoT Vườn Thông Minh (Spring Boot & ESP32)

Dự án này là một nền tảng Internet of Things (IoT) toàn diện, được xây dựng để giám sát và điều khiển một hệ thống vườn thông minh trong thời gian thực.

Hệ thống bao gồm một **Backend Spring Boot** mạnh mẽ, giao tiếp không đồng bộ qua **MQTT** với các thiết bị **ESP32**, lưu trữ dữ liệu vào **MySQL**, sử dụng **Redis** để caching trạng thái, và cung cấp một **API** để gửi lệnh điều khiển.

Một tính năng nổi bật là việc tích hợp **Spring AI**, cho phép người dùng hỏi-đáp và ra lệnh cho khu vườn bằng ngôn ngữ tự nhiên.

## 1\. Kiến trúc Hệ thống 🏛️

Hệ thống được thiết kế theo kiến trúc hướng sự kiện (Event-Driven) với hai luồng hoạt động chính:

1.  **Luồng Dữ liệu (Device-to-Cloud):** Dữ liệu từ cảm biến được gửi không đồng bộ.
    `ESP32 -> MQTT Broker (Mosquitto) -> Spring Boot (Integration) -> Redis (Cache) & MySQL (Log) -> WebSocket -> Dashboard`

2.  **Luồng Điều khiển (User-to-Device):** Lệnh từ người dùng được gửi xuống thiết bị.
    `User -> Spring Boot (REST API) -> Spring AI (Xử lý lệnh) -> MQTT Broker -> ESP32`

-----

## 2\. Tính năng Chính ⭐

* **Giám sát Thời gian thực:** Cập nhật liên tục nhiệt độ, độ ẩm không khí, độ ẩm đất, và cường độ ánh sáng.
* **Điều khiển Từ xa:** Bật/tắt máy bơm nước thông qua REST API.
* **Logic Tưới Tự động:** Firmware của ESP32 tự động tưới dựa trên độ ẩm đất và ánh sáng (chỉ khi ở chế độ `AUTO`).
* **Lưu trữ Lịch sử:** Toàn bộ dữ liệu cảm biến được lưu trữ vào MySQL để phân tích sau này (quản lý schema bằng **Flyway**).
* **Caching Hiệu năng cao:** Trạng thái *tức thời* của mọi thiết bị được cache trên **Redis**, giúp truy vấn API (`/state`) cực nhanh.
* **Trợ lý AI (Spring AI):**
    * Hỏi-đáp về trạng thái vườn ("Nhiệt độ hôm nay thế nào?").
    * Điều khiển bằng ngôn ngữ tự nhiên ("Bật máy bơm cho tôi").
* **Tài liệu API (Swagger):** Tích hợp OpenAPI 3 để tài liệu hóa và kiểm thử API một cách trực quan.

-----

## 3\. Công nghệ sử dụng 🛠️

| Lĩnh vực | Công nghệ |
| :--- | :--- |
| **Backend** | Spring Boot, Spring Integration (MQTT), Spring Data JPA, Spring Cache (Redis), Spring WebSocket (STOMP), Spring AI, MapStruct |
| **Cơ sở dữ liệu** | MySQL (Lưu trữ lịch sử), Flyway (Quản lý Schema) |
| **Cache** | Redis (Lưu trạng thái tức thời) |
| **Broker** | Mosquitto (Broker MQTT cục bộ, có xác thực) |
| **Thiết bị (Firmware)** | C++ (Arduino IDE), ESP32 |
| **Giao thức** | MQTT, TCP/IP, HTTP/REST, WebSocket |
| **Thư viện Arduino** | `PubSubClient`, `ArduinoJson`, `DHT`, `BH1750` |
| **Tài liệu API** | OpenAPI 3 (Springdoc / Swagger UI) |

-----

## 4\. Hướng dẫn Cài đặt & Chạy thử (Getting Started)

### Yêu cầu Tiên quyết

1.  **Java JDK 17+** và **Maven 3+**
2.  **Cơ sở dữ liệu MySQL** (ví dụ: `smart_garden_db`)
3.  **Redis Server** (chạy ở port 6379)
4.  **Mosquitto MQTT Broker** (chạy ở port 1883)
5.  **Arduino IDE** và **ESP32**
6.  **MQTTX** (hoặc MQTT Explorer) và **Postman** (để kiểm thử)

### Bước 1: Cấu hình Mosquitto Broker (Rất quan trọng)

Backend được cấu hình để kết nối đến `localhost:1883` với `username: iot_admin` và `password: 123456`.

1.  Cài đặt Mosquitto.
2.  Tạo file `password.txt` với nội dung: `iot_admin:123456`
3.  Chạy lệnh `mosquitto_passwd -U password.txt` để băm mật khẩu.
4.  Tạo file `mosquitto.conf` với nội dung:
    ```ini
    allow_anonymous false
    password_file /đường/dẫn/tới/file/password.txt
    listener 1883
    ```
5.  Chạy Mosquitto: `mosquitto -c mosquitto.conf`

### Bước 2: Cấu hình Backend (Spring Boot)

1.  Clone repository.
2.  Mở `src/main/resources/application.yml`.
3.  Cập nhật thông tin `spring.datasource` (username/password MySQL của bạn).
4.  Đảm bảo `spring.redis` và `mqtt` trỏ đúng (thường là `localhost`).
5.  Thêm API Key của bạn vào `spring.ai.openai.api-key` (nếu bạn muốn test AI).

### Bước 3: Chạy Backend

Mở terminal và chạy:

```bash
mvn spring-boot:run
```

**Flyway** sẽ tự động chạy và tạo các bảng CSDL (từ `V1__...Schema.sql`). Backend sẽ kết nối tới Mosquitto.

### Bước 4: Nạp Firmware (ESP32)

1.  Mở file `.ino` bằng Arduino IDE.
2.  Cài đặt các thư viện: `PubSubClient`, `ArduinoJson`, `DHT`, `BH1750`.
3.  Cập nhật các thông số sau trong code:
    ```cpp
    const char* WIFI_SSID = "TEN_WIFI_CUA_BAN";
    const char* WIFI_PASS = "MAT_KHAU_WIFI";
    const char* DEVICE_UID = "ESP32_GARDEN_01"; 

    // Đảm bảo khớp với Mosquitto
    const char* MQTT_USER = "iot_admin";
    const char* MQTT_PASS = "123456";
    ```
4.  Cắm ESP32, chọn đúng cổng COM và nạp code.

-----

## 5\. Hướng dẫn Test (Không cần Frontend)

### Bước 1: Kết nối MQTTX (Giả lập Client)

1.  Mở MQTTX, tạo kết nối mới.
2.  **Host:** `localhost`
3.  **Port:** `1883`
4.  **Username:** `iot_admin`
5.  **Password:** `123456`
6.  Nhấn **Connect**.

### Bước 2: Đăng ký Thiết bị (Postman)

Để backend biết về thiết bị này:

* `POST http://localhost:8080/api/v1/devices`
* Body (JSON):
  ```json
  {
      "deviceUid": "ESP32_GARDEN_01",
      "name": "Vườn Thử Nghiệm"
  }
  ```

### Bước 3: Kiểm tra Luồng Data (ESP32 -\> API)

1.  Mở Serial Monitor trong Arduino IDE. Bạn sẽ thấy ESP32 kết nối và bắt đầu gửi `telemetry` và `state`.
2.  Vào **Postman**, gọi:
    `GET http://localhost:8080/api/v1/devices/ESP32_GARDEN_01/state`
3.  **Kết quả:** Bạn sẽ thấy JSON trạng thái tức thời (từ Redis) mà ESP32 vừa gửi.
    ```json
    {
        "deviceUid": "ESP32_GARDEN_01",
        "status": "online",
        "controlMode": "AUTO",
        "pumpState": "OFF",
        "sensors": { ... }
    }
    ```

### Bước 4: Kiểm tra Luồng Điều khiển (API -\> ESP32)

1.  Trong **MQTTX**, subscribe (đăng ký) vào topic: `smartgarden/device/ESP32_GARDEN_01/command`
2.  Trong **Postman**, gửi một lệnh:
    * `POST http://localhost:8080/api/v1/devices/ESP32_GARDEN_01/command`
    * Body (JSON):
      ```json
      {
          "action": "SET_MODE",
          "payload": {
              "value": "MANUAL"
          }
      }
      ```
3.  **Kết quả:** Ngay lập tức, bạn sẽ thấy tin nhắn lệnh này xuất hiện trong **MQTTX**. ESP32 (nếu đang chạy) cũng sẽ nhận được và chuyển sang chế độ `MANUAL`.

### Bước 5: Kiểm tra AI (Postman)

* `POST http://localhost:8080/api/v1/ai/chat`
* Body (JSON):
  ```json
  {
      "message": "Nhiệt độ vườn của tôi là bao nhiêu?"
  }
  ```
* **Kết quả:** AI sẽ trả lời (ví dụ: "Nhiệt độ hiện tại là 28.5°C.") sau khi tự động gọi hàm `getDeviceState` nội bộ.

-----

## 6\. Tài liệu API (Swagger)

Khi backend đang chạy, bạn có thể truy cập tài liệu API tương tác tại:

**`http://localhost:8080/swagger-ui.html`**