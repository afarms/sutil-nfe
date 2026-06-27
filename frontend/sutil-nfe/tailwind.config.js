/** @type {import('tailwindcss').Config} */
export default {
  // No v4 o rastreamento de conteúdo é automático,
  // mas manter o 'content' ajuda na compatibilidade com plugins.
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}