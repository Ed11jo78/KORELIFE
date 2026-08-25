@echo off
echo ========================================================
echo Iniciando KoreLife App en Expo Go (SDK 54.0.0 Oficial)
echo Puerto: 8088 ^| IP: 192.168.3.3
echo ========================================================
cd /d "%~dp0"
set REACT_NATIVE_PACKAGER_HOSTNAME=192.168.3.3
npx expo start --port 8088 --clear
pause