@echo off
chcp 65001 > nul
title KoreLife - Servidor Web & iPhone PWA
echo ========================================================
echo         KORELIFE - SERVIDOR WEB PARA IPHONE (PWA)
echo ========================================================
echo.
echo Iniciando servidor en puerto 8090...
echo.
cd /d "%~dp0"
node server.js
pause
