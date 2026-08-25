@echo off
echo ========================================================
echo Compilando APK de Android con EAS Build (Instalador Directo)
echo ========================================================
cd /d "%~dp0"
npx eas-cli build --platform android --profile preview
