@echo off
set "EAS_NO_VCS=1"
cd /d "%~dp0"
cls
echo ========================================================
echo        KORELIFE - PUBLICAR EN LA NUBE (EAS UPDATE)
echo ========================================================
echo.
echo 1. Si tienes tu Token de Expo:
echo    Entra a: https://expo.dev/settings/access-tokens
echo.
set /p USER_TOKEN="Pega tu Token de Expo (o presiona Enter si ya iniciaste sesion): "
if not "%USER_TOKEN%"=="" (
    set "EXPO_TOKEN=%USER_TOKEN%"
)
echo.
echo Publicando actualizacion en la rama preview de Expo...
echo.
call npx eas-cli update --branch preview --message "Publicacion KoreLife"
echo.
echo ========================================================
echo Publicacion completada. Ya puedes abrirlo en Expo Go
echo incluso con la computadora apagada.
echo ========================================================
pause
