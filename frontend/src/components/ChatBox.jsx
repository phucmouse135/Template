import React, { useEffect, useRef } from 'react';
import Message from './Message';

const ChatBox = ({ messages, isThinking }) => {
  const chatBoxRef = useRef(null);

  // Tự động cuộn xuống dưới khi có tin nhắn mới
  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scrollTop = chatBoxRef.current.scrollHeight;
    }
  }, [messages, isThinking]);

  return (
    <div id="chat-box" ref={chatBoxRef} className="chat-box mb-4">
      {messages.map((msg, index) => (
        <Message key={index} text={msg.text} sender={msg.sender} />
      ))}
      {isThinking && <Message text="Synthia đang suy nghĩ..." sender="ai" isThinking={true} />}
    </div>
  );
};

export default ChatBox;