package com.example.demo.service;

import com.example.demo.dto.DeviceStateDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${spring.mail.username}")
    private String toEmail;


    // SimpMessagingTemplate là thành phần cốt lõi để gửi tin nhắn qua WebSocket
    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;

    /**
     * Gửi cập nhật trạng thái tức thời của thiết bị tới tất cả các client đã subscribe.
     *
     * @param state DeviceStateDTO đã được cập nhật từ luồng MQTT/Redis.
     */
    public void broadcastDeviceUpdate(DeviceStateDTO state) {
        // Địa chỉ topic mà client cần subscribe: /topic/device/{deviceUid}
        String destination = "/topic/device/" + state.getDeviceUid();

        try {
            // Gửi toàn bộ DeviceStateDTO (dưới dạng JSON) tới topic.
            // Broker (được cấu hình bằng enableSimpleBroker("/topic")) sẽ phân phối tin nhắn này.
            messagingTemplate.convertAndSend(destination, state);
            log.debug("Broadcasted device state update to topic: {}", destination);
        } catch (Exception e) {
            log.error("Failed to broadcast WebSocket message to {}: {}", destination, e.getMessage());
            // Xử lý lỗi (ví dụ: client disconnect)
        }
    }

    public void broadcastAIMessage(String aiMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(toEmail);
            helper.setTo("tatruongvuptit@gmail.com");
            helper.setSubject("Thông báo biến động từ AI");

            String htmlContent = "<html><body>"
                    + "<h3>Thông báo từ hệ thống AI</h3>"
                    + "<p>" + aiMessage + "</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);
            // 3. Gửi tin nhắn
            mailSender.send(message);
            log.debug("Broadcasted AI message via HTML email to: {}", toEmail);
        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}