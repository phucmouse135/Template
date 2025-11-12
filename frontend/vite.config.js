import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Proxy các yêu cầu /api đến backend Spring Boot
      '/api': {
        target: 'http://localhost:8080', // Địa chỉ backend của bạn
        changeOrigin: true,
      },
    },
  },
})