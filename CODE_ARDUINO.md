## 2\. Hướng dẫn Test Backend (Không cần Frontend)

Bạn sẽ cần hai công cụ:

1.  **Postman** (hoặc cURL): Để test REST API.
2.  **MQTTX** (hoặc MQTT Explorer): Để giả lập thiết bị ESP32 và kiểm tra lệnh điều khiển.

### Bước 0: Khởi động

1.  Chạy MySQL và Redis.
2.  Chạy ứng dụng Spring Boot.
3.  Mở Postman và MQTTX.

### Bước 1: Giả lập ESP32 và Gửi Dữ liệu

Trong **MQTTX**:

1.  Tạo kết nối mới đến broker: `broker.hivemq.com:1883`.
2.  **Gửi Status (Online):**
    * **Topic:** `smartgarden/device/ESP32_GARDEN_01/status`
    * **Payload (JSON):** `{"status": "online"}`
    * Bật **Retain** (Giữ lại). Bấm **Publish**.
3.  **Gửi State (Trạng thái):**
    * **Topic:** `smartgarden/device/ESP32_GARDEN_01/state`
    * **Payload (JSON):** `{"control_mode": "AUTO", "pump_state": "OFF"}`
    * Bật **Retain**. Bấm **Publish**.
4.  **Gửi Telemetry (Cảm biến):**
    * **Topic:** `smartgarden/device/ESP32_GARDEN_01/telemetry`
    * **Payload (JSON):** `{"sensors": {"temperature": 28.5, "air_humidity": 75.0, "light": 9000.0, "soil_moisture": 65.0}}`
    * Bấm **Publish** (Không cần Retain).

### Bước 2: Đăng ký Thiết bị qua API

Trong **Postman**:

1.  **Request:** `POST http://localhost:8080/api/v1/devices`
2.  **Body (raw, JSON):**
    ```json
    {
        "deviceUid": "ESP32_GARDEN_01",
        "name": "Vườn Tầng Thượng"
    }
    ```
3.  Bấm **Send**. Bạn sẽ thấy thiết bị mới được tạo.

### Bước 3: Test API Đọc Dữ liệu

Trong **Postman**:

1.  **Request:** `GET http://localhost:8080/api/v1/devices/ESP32_GARDEN_01/state`
2.  **Kết quả:** Bạn *phải* thấy dữ liệu JSON mà bạn đã gửi bằng MQTTX ở Bước 1. Điều này xác nhận **MQTT -\> Spring Boot -\> Redis -\> API** hoạt động.
    ```json
    {
        "deviceUid": "ESP32_GARDEN_01",
        "status": "online",
        "controlMode": "AUTO",
        "pumpState": "OFF",
        "sensors": {
            "temperature": 28.5,
            // ...
        }
    }
    ```
3.  **Request:** `GET http://localhost:8080/api/v1/devices/ESP32_GARDEN_01/history?from=...&to=...`
4.  **Kết quả:** Bạn sẽ thấy dữ liệu `telemetry` đã được lưu vào MySQL.

### Bước 4: Test API Điều khiển (Quan trọng nhất)

1.  Trong **MQTTX**:
    * Tạo một **Subscription** (Đăng ký) mới.
    * **Topic:** `smartgarden/device/ESP32_GARDEN_01/command`
2.  Trong **Postman**:
    * **Request:** `POST http://localhost:8080/api/v1/devices/ESP32_GARDEN_01/command`
    * **Body (raw, JSON):**
      ```json
      {
          "action": "SET_PUMP",
          "payload": {
              "value": "ON"
          }
      }
      ```
    * Bấm **Send**.
3.  **Kiểm tra Kết quả:**
    * Ngay lập tức, bạn *phải* thấy tin nhắn trên xuất hiện trong cửa sổ **MQTTX** (nơi bạn đã subscribe).
    * Điều này xác nhận luồng **API -\> Spring Boot -\> MQTT Outbound -\> Broker** hoạt động.

-----

## 3\. Code Arduino IDE cho ESP32

Đây là code đầy đủ, sử dụng các thư viện chuẩn và logic non-blocking (không dùng `delay()`).

**Yêu cầu Thư viện (Cài đặt qua Arduino Library Manager):**

1.  `PubSubClient` (bởi Nick O'Leary)
2.  `ArduinoJson` (bởi Benoit Blanchon)
3.  `DHT sensor library` (bởi Adafruit)
4.  `Adafruit Unified Sensor` (bởi Adafruit)
5.  `BH1750` (bởi Christopher Laws)

<!-- end list -->

```cpp
/*
 * FIRMWARE VƯỜN THÔNG MINH (ESP32)
 * Tương thích với Backend Spring Boot (Public)
 */

#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <BH1750.h>
#include <DHT.h>

// --- 1. CẤU HÌNH (THAY ĐỔI) ---
const char* WIFI_SSID = "TEN_WIFI_CUA_BAN";
const char* WIFI_PASS = "MAT_KHAU_WIFI";
const char* MQTT_BROKER = "broker.hivemq.com";
const int MQTT_PORT = 1883;

// !! QUAN TRỌNG: ID này phải khớp với API
const char* DEVICE_UID = "ESP32_GARDEN_01"; 

// --- 2. CẤU HÌNH KỸ THUẬT ---
// MQTT Topics
const char* TOPIC_TELEMETRY;
const char* TOPIC_STATE;
const char* TOPIC_STATUS;
const char* TOPIC_COMMAND;

// Chân GPIO (Ví dụ)
#define DHT_PIN 4
#define DHT_TYPE DHT11
#define RELAY_PIN 5
#define SOIL_PIN 34 // Chân ADC1_CH6 (Analog)

// Ngưỡng nghiệp vụ (Cần hiệu chỉnh thực tế)
#define SOIL_MOISTURE_DRY 2500     // Giá trị ADC khi khô
#define SOIL_MOISTURE_WET 1000     // Giá trị ADC khi ướt
#define AUTO_MODE_PUMP_DURATION_MS 30000 // Tưới tự động trong 30 giây
#define AUTO_MODE_LIGHT_MIN_LUX 100      // Chỉ tưới khi trời sáng

// Timer (Non-blocking)
unsigned long lastTelemetryPublish = 0;
unsigned long lastSensorRead = 0;
unsigned long pumpAutoStartTime = 0;
#define SENSOR_READ_INTERVAL 5000     // Đọc cảm biến mỗi 5 giây
#define TELEMETRY_PUBLISH_INTERVAL 60000 // Gửi dữ liệu mỗi 60 giây

// --- 3. KHỞI TẠO ĐỐI TƯỢNG ---
WiFiClient espClient;
PubSubClient mqttClient(espClient);
DHT dht(DHT_PIN, DHT_TYPE);
BH1750 lightMeter(0x23);

// --- 4. BIẾN TRẠNG THÁI (STATE) ---
// Cảm biến
float g_temperature = -99;
float g_air_humidity = -99;
float g_light = -99;
float g_soil_moisture_percent = -99;

// Điều khiển
bool g_pumpState = false; // false = OFF, true = ON
String g_controlMode = "AUTO"; // "AUTO" hoặc "MANUAL"

// --- 5. HÀM KHỞI TẠO ĐỘNG TOPIC ---
// (Phải gọi trong setup() sau khi có DEVICE_UID)
void initializeTopics() {
    static char topicTelemetry[100], topicState[100], topicStatus[100], topicCommand[100];
    snprintf(topicTelemetry, 100, "smartgarden/device/%s/telemetry", DEVICE_UID);
    snprintf(topicState, 100, "smartgarden/device/%s/state", DEVICE_UID);
    snprintf(topicStatus, 100, "smartgarden/device/%s/status", DEVICE_UID);
    snprintf(topicCommand, 100, "smartgarden/device/%s/command", DEVICE_UID);

    TOPIC_TELEMETRY = topicTelemetry;
    TOPIC_STATE = topicState;
    TOPIC_STATUS = topicStatus;
    TOPIC_COMMAND = topicCommand;
}

// --- 6. HÀM KẾT NỐI ---
void setupWifi() {
    delay(10);
    Serial.print("\nConnecting to ");
    Serial.println(WIFI_SSID);
    WiFi.begin(WIFI_SSID, WIFI_PASS);
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    Serial.println("\nWiFi connected. IP address: ");
    Serial.println(WiFi.localIP());
}

// Hàm nhận tin nhắn lệnh
void mqttCallback(char* topic, byte* payload, unsigned int length) {
    Serial.print("Message arrived [");
    Serial.print(topic);
    Serial.print("] ");

    // Phân tích JSON payload
    StaticJsonDocument<256> doc;
    deserializeJson(doc, payload, length);
    
    const char* action = doc["action"]; // "SET_PUMP" hoặc "SET_MODE"

    if (strcmp(action, "SET_MODE") == 0) {
        String newMode = doc["payload"]["value"];
        if (newMode == "AUTO" || newMode == "MANUAL") {
            g_controlMode = newMode;
            Serial.print("Mode changed to: ");
            Serial.println(g_controlMode);
            
            // Nếu chuyển về AUTO, tắt bơm (để logic AUTO quyết định)
            if (g_controlMode == "AUTO") {
                setPumpState(false);
            }
        }
    } 
    else if (strcmp(action, "SET_PUMP") == 0) {
        // Chỉ cho phép điều khiển bơm nếu ở chế độ MANUAL
        if (g_controlMode == "MANUAL") {
            String pumpCmd = doc["payload"]["value"];
            setPumpState(pumpCmd == "ON");
        } else {
            Serial.println("Ignored SET_PUMP (not in MANUAL mode)");
        }
    }

    // Phản hồi lại trạng thái mới
    publishState();
}

void reconnectMqtt() {
    while (!mqttClient.connected()) {
        Serial.print("Attempting MQTT connection...");
        
        // Cài đặt "Last Will and Testament" (LWT)
        // Nếu ESP32 mất kết nối, Broker sẽ tự gửi "offline"
        char lwtPayload[32];
        snprintf(lwtPayload, 32, "{\"status\": \"offline\"}");
        
        if (mqttClient.connect(DEVICE_UID, TOPIC_STATUS, 1, true, lwtPayload)) {
            Serial.println("connected");
            
            // Gửi trạng thái "online" (Retain = true)
            char onlinePayload[32];
            snprintf(onlinePayload, 32, "{\"status\": \"online\"}");
            mqttClient.publish(TOPIC_STATUS, onlinePayload, true);

            // Subcribe vào topic lệnh
            mqttClient.subscribe(TOPIC_COMMAND);
            
            // Gửi trạng thái hiện tại ngay khi kết nối
            publishState();
            publishTelemetry();

        } else {
            Serial.print("failed, rc=");
            Serial.print(mqttClient.state());
            Serial.println(" try again in 5 seconds");
            delay(5000); // Chỉ delay khi đang cố gắng kết nối lại
        }
    }
}

// --- 7. HÀM NGHIỆP VỤ ---

// Hàm điều khiển relay và cập nhật state
void setPumpState(bool newState) {
    if (g_pumpState != newState) {
        g_pumpState = newState;
        digitalWrite(RELAY_PIN, newState ? HIGH : LOW); // Giả sử Relay kích HIGH
        Serial.print("Pump turned ");
        Serial.println(newState ? "ON" : "OFF");
        
        if (newState) {
            pumpAutoStartTime = millis(); // Bắt đầu đếm giờ nếu bơm BẬT
        }
        
        publishState(); // Gửi trạng thái mới ngay lập tức
    }
}

void readSensors() {
    g_temperature = dht.readTemperature();
    g_air_humidity = dht.readHumidity();
    g_light = lightMeter.readLightLevel();
    
    int soil_raw = analogRead(SOIL_PIN);

    // Kiểm tra lỗi đọc DHT
    if (isnan(g_temperature) || isnan(g_air_humidity)) {
        Serial.println("Failed to read from DHT sensor!");
        g_temperature = -99; g_air_humidity = -99;
    }
    
    // Chuyển đổi giá trị ADC ẩm đất sang %
    long percent = map(soil_raw, SOIL_MOISTURE_DRY, SOIL_MOISTURE_WET, 0, 100);
    g_soil_moisture_percent = constrain(percent, 0, 100);

    Serial.printf("Sensors: T=%.1fC, AH=%.1f%%, L=%.1flx, SM=%.1f%%\n",
        g_temperature, g_air_humidity, g_light, g_soil_moisture_percent);
}

// Logic tưới tự động
void runAutoLogic() {
    if (g_controlMode != "AUTO") {
        return; // Không làm gì nếu đang ở chế độ thủ công
    }

    // Logic TẮT (ưu tiên)
    if (g_pumpState == true) {
        if (millis() - pumpAutoStartTime >= AUTO_MODE_PUMP_DURATION_MS) {
            Serial.println("[AUTO] Pump duration elapsed. Turning OFF.");
            setPumpState(false);
        }
    }
    // Logic BẬT
    else {
        // Điều kiện: Đất khô VÀ trời sáng
        if (g_soil_moisture_percent < 30.0 // Ví dụ: tưới khi dưới 30%
            && g_light > AUTO_MODE_LIGHT_MIN_LUX) {
            
            Serial.println("[AUTO] Conditions met (Dry & Light). Turning ON.");
            setPumpState(true); // Hàm này sẽ tự động bắt đầu timer
        }
    }
}

// --- 8. HÀM GỬI DỮ LIỆU (PUBLISH) ---

void publishTelemetry() {
    StaticJsonDocument<256> doc;
    JsonObject sensors = doc.createNestedObject("sensors");
    
    sensors["temperature"] = g_temperature;
    sensors["air_humidity"] = g_air_humidity;
    sensors["light"] = g_light;
    sensors["soil_moisture"] = g_soil_moisture_percent;

    char buffer[256];
    serializeJson(doc, buffer);
    
    Serial.print("Publishing telemetry: ");
    Serial.println(buffer);
    mqttClient.publish(TOPIC_TELEMETRY, buffer);
}

void publishState() {
    StaticJsonDocument<128> doc;
    doc["control_mode"] = g_controlMode;
    doc["pump_state"] = g_pumpState ? "ON" : "OFF";

    char buffer[128];
    serializeJson(doc, buffer);
    
    Serial.print("Publishing state: ");
    Serial.println(buffer);
    mqttClient.publish(TOPIC_STATE, buffer, true); // Gửi và giữ lại (retain)
}

// --- 9. SETUP & LOOP ---
void setup() {
    Serial.begin(115200);
    
    // Khởi tạo chân
    pinMode(RELAY_PIN, OUTPUT);
    digitalWrite(RELAY_PIN, LOW); // Đảm bảo bơm tắt
    pinMode(SOIL_PIN, INPUT);
    
    // Khởi tạo cảm biến
    dht.begin();
    Wire.begin();
    lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE);
    
    // Khởi tạo tên Topic
    initializeTopics();

    // Kết nối mạng
    setupWifi();
    mqttClient.setServer(MQTT_BROKER, MQTT_PORT);
    mqttClient.setCallback(mqttCallback);
    
    // Đọc cảm biến lần đầu
    readSensors();
}

void loop() {
    // 1. Duy trì kết nối
    if (WiFi.status() != WL_CONNECTED) {
        setupWifi();
    }
    if (!mqttClient.connected()) {
        reconnectMqtt();
    }
    mqttClient.loop(); // Rất quan trọng: Xử lý các message đến

    unsigned long now = millis();

    // 2. Đọc cảm biến (theo chu kỳ)
    if (now - lastSensorRead >= SENSOR_READ_INTERVAL) {
        lastSensorRead = now;
        readSensors();
    }
    
    // 3. Chạy logic nghiệp vụ (Tự động)
    runAutoLogic();

    // 4. Gửi dữ liệu (theo chu kỳ)
    if (now - lastTelemetryPublish >= TELEMETRY_PUBLISH_INTERVAL) {
        lastTelemetryPublish = now;
        publishTelemetry();
    }
}
```