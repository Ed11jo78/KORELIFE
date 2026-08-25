/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{js,jsx,ts,tsx}",
    "./src/**/*.{js,jsx,ts,tsx}",
    "./components/**/*.{js,jsx,ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#4f46e5', // indigo-600
          light: '#818cf8',
          dark: '#3730a3',
        },
        accent: {
          DEFAULT: '#10b981', // emerald-500
          light: '#34d399',
          dark: '#047857',
        },
      },
    },
  },
  plugins: [],
}
