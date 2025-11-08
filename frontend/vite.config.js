import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// ✅ Vite configuration
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Your Spring Boot backend
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
