@echo off
cd /d "%~dp0"
echo Compilando aplicacion web...
call npx expo export --platform web
echo.
echo Subiendo directamente a Vercel...
npx vercel --prod --yes
pause
