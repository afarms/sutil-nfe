import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    // Aponta para a pasta estática do Java
    outDir: '../../src/main/resources/static',
    // Limpa a pasta antes de buildar (recomendado)
    emptyOutDir: true,
  },
})

