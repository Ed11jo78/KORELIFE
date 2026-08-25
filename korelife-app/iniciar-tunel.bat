@echo off
cd /d "%~dp0"
cls
echo ========================================================
echo        KORELIFE - SERVIDOR EN MODO TUNEL (EXPO GO)
echo ========================================================
echo.
echo Iniciando tunel global para conectar desde cualquier red...
echo.
npx expo start --tunnel
pause
