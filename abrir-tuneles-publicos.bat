@echo off
echo ========================================================
echo Abriendo Tunel Publico HTTPS para acceder desde cualquier red
echo ========================================================
echo.
echo Panel Admin Web:
start cmd /k "lt --port 5173 --subdomain korelife-admin"
echo App Movil y Web:
start cmd /k "lt --port 8081 --subdomain korelife-app"
echo.
echo Los tuneles han sido iniciados. Revisa las nuevas ventanas.
pause
