@echo off
echo ========================================================
echo Iniciando KoreLife App en MODO ONLINE (Cualquier Red)
echo ========================================================
cd /d "%~dp0"
npx expo start --tunnel --clear
pause
