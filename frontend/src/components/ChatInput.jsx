import React, { useState, useEffect } from 'react';
import useSpeechRecognition from '../hooks/useSpeechRecognition';

const ChatInput = ({ onSendMessage }) => {
  const [inputValue, setInputValue] = useState('');
  const {
    text: speechText,
    isListening,
    error: speechError,
    startListening,
    stopListening,
    hasRecognitionSupport,
  } = useSpeechRecognition();

  // Cập nhật ô input khi có kết quả từ giọng nói
  useEffect(() => {
    if (speechText) {
      setInputValue(speechText);
      // Tự động gửi tin nhắn sau khi nhận diện xong
      setTimeout(() => handleSend(), 100);
    }
  }, [speechText]);

  const handleSend = () => {
    onSendMessage(inputValue);
    setInputValue('');
  };

  const handleMicClick = () => {
    if (isListening) {
      stopListening();
    } else {
      startListening();
    }
  };

  return (
    <footer className="chat-footer">
      <p id="mic-status" className="text-sm text-gray-400 text-center h-5 mb-2">
        {isListening ? "Tôi đang nghe..." : speechError || ''}
      </p>

      <div className="flex items-center">
        <input
          type="text"
          id="chat-input"
          className="chat-input"
          placeholder="Nhập tin nhắn hoặc nhấn mic..."
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();
              handleSend();
            }
          }}
        />

        <button
          id="send-btn"
          onClick={handleSend}
          className="ml-3 btn-send p-3 rounded-full shadow-md transition duration-200 flex-shrink-0"
          aria-label="Send message"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
        </button>

        {hasRecognitionSupport && (
          <button
            id="mic-btn"
            onClick={handleMicClick}
            className={`ml-3 p-3 rounded-full transition duration-200 flex-shrink-0 ${
              isListening ? 'mic-active bg-red-500' : 'bg-red-600 hover:bg-red-700'
            }`}
            aria-pressed={isListening}
            aria-label={isListening ? 'Stop recording' : 'Start recording'}
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 1v11m0 0a3 3 0 003 3h0a3 3 0 003-3M5 11a7 7 0 0014 0" />
            </svg>
          </button>
        )}
      </div>
    </footer>
  );
};

export default ChatInput;