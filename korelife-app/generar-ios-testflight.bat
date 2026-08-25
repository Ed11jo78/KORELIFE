@echo off
echo ========================================================
echo Compilando para Apple TestFlight / App Store
echo ========================================================
echo.
echo Generando build de produccion para distribuir mediante TestFlight...
echo.
cd /d "%~dp0"
npx eas-cli build --platform ios --profile production
pause
