import React, { useState } from 'react';
import Header from './src/components/Header.jsx';
import ChatBox from './src/components/ChatBox.jsx';
import ChatInput from './src/components/ChatInput.jsx';
import { callAiService } from './src/services/aiService.js';

function App() {
  const [messages, setMessages] = useState([
    { text: 'Chào bạn, tôi có thể giúp gì cho vườn của bạn? Bạn có thể hỏi tôi "Kiểm tra vườn" hoặc nhấn nút micro để ra lệnh.', sender: 'ai' }
  ]);
  const [isThinking, setIsThinking] = useState(false);
  const deviceUid = "ESP32_GARDEN_01"; // Có thể lấy từ URL hoặc config

  const handleSendMessage = async (messageText) => {
    if (messageText.trim() === "") return;

    // Thêm tin nhắn của người dùng vào danh sách
    setMessages(prev => [...prev, { text: messageText, sender: 'user' }]);
    setIsThinking(true);

    try {
      const aiResponse = await callAiService(messageText, deviceUid);
      // Thêm tin nhắn của AI vào danh sách
      setMessages(prev => [...prev, { text: aiResponse.response, sender: 'ai' }]);
    } catch (error) {
      console.error("Lỗi khi gọi AI Service:", error);
      setMessages(prev => [...prev, { text: `Xin lỗi, tôi gặp lỗi: ${error.message}. Vui lòng thử lại.`, sender: 'ai' }]);
    } finally {
      setIsThinking(false);
    }
  };

  return (
    <div id="app-root">
      <div className="app-container">
        <div className="chat-panel">
          <Header deviceUid={deviceUid} />
          <ChatBox messages={messages} isThinking={isThinking} />
          <ChatInput onSendMessage={handleSendMessage} />
        </div>
      </div>
    </div>
  );
}

export default App;