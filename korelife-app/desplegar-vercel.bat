@echo off
cd /d "%~dp0"
cls
echo ========================================================
echo        KORELIFE - DESPLIEGUE GRATUITO EN VERCEL
echo ========================================================
echo.
echo Paso 1: Iniciando sesion en Vercel...
echo.
call npx vercel login
echo.
echo Paso 2: Compilando archivos web...
echo.
call npx expo export --platform web
echo.
echo Paso 3: Subiendo a Vercel...
echo.
call npx vercel --prod --yes
echo.
echo ========================================================
echo Despliegue finalizado.
echo ========================================================
pause
