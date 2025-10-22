// src/main/java/com/example/smartgarden/config/MqttConfig.java

package com.example.demo.configuration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MqttConfig {

    // Lấy giá trị từ application.yml
    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.inbound-topic}")
    private String inboundTopic; // "smartgarden/device/+/+"

    /**
     * 1. Factory tạo MqttClient
     * Cấu hình các thông số kết nối cơ bản đến Broker.
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});

        factory.setConnectionOptions(options);
        // Có thể cấu hình username/password nếu broker yêu cầu
        return factory;
    }

    /**
     * 2. Kênh nhận dữ liệu từ MQTT
     * Là DirectChannel, tin nhắn được xử lý đồng bộ trên thread nhận.
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * 3. Adapter lắng nghe MQTT
     * Đăng ký với Broker để lắng nghe tin nhắn từ topic đã cấu hình.
     */
    @Bean
    public MessageProducer inbound() {
        // Tạo adapter với Client ID khác biệt cho luồng inbound
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId + "_inbound",
                        mqttClientFactory(),
                        inboundTopic
                );

        // Thiết lập thời gian chờ hoàn thành
        adapter.setCompletionTimeout(5000);

        // Converter chuyển đổi payload thô sang String (hoặc byte[] nếu không dùng Default)
        adapter.setConverter(new DefaultPahoMessageConverter());

        // Chất lượng dịch vụ (QoS)
        adapter.setQos(1);

        // Đặt kênh đầu ra là kênh sẽ nhận tin nhắn
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    // Lưu ý: Các Bean cho Outbound (Giai đoạn 4) sẽ được thêm vào sau.
}