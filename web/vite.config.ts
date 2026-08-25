/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import {fileURLToPath, URL} from 'node:url'

const hmrClientPort = Number(process.env.VITE_HMR_CLIENT_PORT ?? 8100)

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    hmr: {
      host: 'localhost',
      clientPort: hmrClientPort,
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
