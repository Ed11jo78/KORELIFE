@echo off
cd /d "%~dp0\korelife-admin"
cls
echo ========================================================
echo        KORELIFE - PANEL ADMINISTRATIVO LOCAL
echo ========================================================
echo.
echo Iniciando servidor Vite del panel de administracion...
echo.
npm run dev
pause
