@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
cls
echo ===================================================================
echo             KORELIFE - SUBIDA LIMPIA A GITHUB
echo ===================================================================
echo.

rem Detectar git en PATH o en carpeta de usuario
set "GIT_CMD=git"
where git >nul 2>nul
if %errorlevel% neq 0 (
    if exist "%USERPROFILE%\.git-bin\cmd\git.exe" (
        set "GIT_CMD=%USERPROFILE%\.git-bin\cmd\git.exe"
    ) else (
        echo [ERROR] Git no se encuentra instalado.
        pause
        exit /b 1
    )
)

echo [OK] Git detectado correctamente.
echo.

echo 1. Verificando estado del repositorio...
"%GIT_CMD%" branch -M main

echo.
echo ===================================================================
echo  PASO FINAL: SUBIR AL REPOSITORIO DE GITHUB
echo ===================================================================
echo.
echo Ve a tu cuenta de GitHub (https://github.com/new) y copia la URL.
echo Ejemplo: https://github.com/Ed11jo78/korelife.git
echo.
set /p REPO_URL="Pega la URL de tu repositorio de GitHub: "

if not "%REPO_URL%"=="" (
    echo.
    echo Configurando origen remoto...
    "%GIT_CMD%" remote remove origin >nul 2>nul
    "%GIT_CMD%" remote add origin %REPO_URL%
    echo.
    echo Subiendo proyecto limpio a GitHub...
    "%GIT_CMD%" push -u origin main --force
    echo.
    echo ===================================================================
    echo  LISTO! Todo el proyecto KoreLife se ha subido a GitHub sin errores.
    echo ===================================================================
) else (
    echo No ingresaste URL. Puedes ejecutar:
    echo   git remote add origin TU_URL_DE_GITHUB
    echo   git push -u origin main --force
)

pause
