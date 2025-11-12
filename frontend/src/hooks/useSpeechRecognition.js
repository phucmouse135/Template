import { useState, useEffect, useRef } from 'react';

const useSpeechRecognition = () => {
  const [text, setText] = useState('');
  const [isListening, setIsListening] = useState(false);
  const [error, setError] = useState('');
  const recognitionRef = useRef(null);

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      setError('Trình duyệt không hỗ trợ Speech-to-Text.');
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.lang = 'vi-VN';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = () => {
      setIsListening(true);
      setError('');
    };

    recognition.onend = () => {
      setIsListening(false);
    };

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      setText(transcript);
    };

    recognition.onerror = (event) => {
      console.error("Lỗi SpeechRecognition:", event.error);
      if (event.error === 'not-allowed' || event.error === 'service-not-allowed') {
        setError("Bạn cần cấp quyền truy cập micro.");
      } else {
        setError("Lỗi: " + event.error);
      }
      setIsListening(false);
    };

    recognitionRef.current = recognition;
  }, []);

  const startListening = () => {
    if (recognitionRef.current && !isListening) {
      try {
        setText(''); // Xóa text cũ trước khi nghe
        recognitionRef.current.start();
      } catch (e) {
        console.error("Không thể bắt đầu nhận dạng:", e);
        setError("Không thể bắt đầu nhận dạng giọng nói.");
      }
    }
  };

  const stopListening = () => {
    if (recognitionRef.current && isListening) {
      recognitionRef.current.stop();
    }
  };

  return {
    text,
    isListening,
    error,
    startListening,
    stopListening,
    hasRecognitionSupport: !!recognitionRef.current,
  };
};

export default useSpeechRecognition;