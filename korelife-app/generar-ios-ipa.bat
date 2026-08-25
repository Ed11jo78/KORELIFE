@echo off
setlocal
set "EAS_NO_VCS=1"
cd /d "%~dp0"

echo ========================================================
echo        KORELIFE - GENERADOR DE INSTALADOR .IPA
echo ========================================================
echo.
echo 1. Si tienes tu Token de Expo:
echo    Entra a: https://expo.dev/settings/access-tokens
echo.
echo Presiona Enter para iniciar...
pause > nul
echo.

set /p USER_TOKEN="Pega tu Token de Expo (o presiona Enter si ya iniciaste sesion): "
if not "%USER_TOKEN%"=="" (
    set "EXPO_TOKEN=%USER_TOKEN%"
    echo Token configurado correctamente.
)

echo.
echo ========================================================
echo Iniciando compilacion de KoreLife .IPA en la nube...
echo ========================================================
echo.

call npx eas-cli build --platform ios --profile preview --non-interactive

echo.
echo ========================================================
echo Proceso finalizado.
echo ========================================================
pause
