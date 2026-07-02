/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f5f7ff',
          100: '#ebf0ff',
          200: '#dae3ff',
          300: '#bdcbff',
          400: '#94a7ff',
          500: '#6378ff',
          600: '#3e4eff',
          700: '#2b36f7',
          800: '#2027c9',
          900: '#1e249f',
        }
      }
    },
  },
  plugins: [],
}
