@echo off
setlocal
set "EAS_NO_VCS=1"
cd /d "%~dp0"

echo ========================================================
echo        KORELIFE - CONECTAR CON TOKEN DE EXPO
echo ========================================================
echo.
echo 1. Entra a: https://expo.dev/settings/access-tokens
echo 2. Crea un token con nombre KoreLife y copialo.
echo.
set /p USER_TOKEN="Pega tu token de Expo aqui y presiona Enter: "
if "%USER_TOKEN%"=="" (
    echo No ingresaste ningun token.
    pause
    exit /b
)

set "EXPO_TOKEN=%USER_TOKEN%"
echo.
echo Token configurado con exito.
echo.
echo ========================================================
echo Iniciando compilacion de KoreLife .IPA...
echo ========================================================
echo.
call npx eas-cli build --platform ios --profile preview
pause
