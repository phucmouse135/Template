/**
 * Gọi API chat của backend Spring Boot.
 * @param {string} message - Tin nhắn của người dùng.
 * @param {string} deviceUid - UID của thiết bị.
 * @returns {Promise<object>} - Dữ liệu trả về từ AI.
 */
export const callAiService = async (message, deviceUid) => {
  // Vite proxy sẽ chuyển tiếp yêu cầu này đến backend
  const API_URL = `/api/v1/ai/chat/${deviceUid}`;

  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ message: message }), // Khớp với AiRequest.java
  });

  if (!response.ok) {
    let errorDetail = "Lỗi không xác định";
    try {
      const errData = await response.json();
      errorDetail = errData.message || JSON.stringify(errData);
    } catch (e) {
      errorDetail = response.statusText;
    }
    throw new Error(`HTTP ${response.status}: ${errorDetail}`);
  }

  return await response.json(); // Khớp với ApiResponse.java
};