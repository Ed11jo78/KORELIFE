@echo off
echo ========================================================
echo Compilando paquete para Simulador de iOS (Mac / Xcode)
echo ========================================================
echo.
echo Generando build de simulador (no requiere certificados de firma)...
echo.
cd /d "%~dp0"
npx eas-cli build --platform ios --profile preview-simulator
pause
