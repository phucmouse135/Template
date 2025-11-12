import React from 'react';

const Message = ({ text, sender, isThinking = false }) => {
  const isUser = sender === 'user';

  return (
    <div className={`flex mb-3 ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`message-bubble ${isUser ? 'message-user' : 'message-ai'}`}
      >
        <p className={isThinking ? 'italic text-gray-400' : 'whitespace-pre-wrap'}>{text}</p>
      </div>
    </div>
  );
};

export default Message;