@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
cls
echo ===================================================================
echo             KORELIFE - SUBIDA LIMPIA A GITHUB
echo ===================================================================
echo.
echo 1. Verificando Git en el sistema...
where git >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Git no esta instalado o no se encuentra en el PATH.
    echo Por favor instala Git desde: https://git-scm.com/download/win
    echo O instala GitHub Desktop: https://desktop.github.com/
    echo.
    pause
    exit /b 1
)

echo [OK] Git detectado correctamente.
echo.

echo 2. Inicializando repositorio y configurando rama principal 'main'...
if not exist ".git" (
    git init
)
git branch -M main

echo.
echo 3. Limpiando cache de archivos pesados antiguos (node_modules, dist, .expo)...
git rm -r --cached . >nul 2>nul

echo.
echo 4. Agregando archivos limpios respetando .gitignore...
git add .

echo.
echo 5. Creando commit limpio y optimizado...
git commit -m "feat: KoreLife - Full Stack Mobile App, Admin Portal and Database"

echo.
echo ===================================================================
echo  PASO FINAL: CONFIGURACION DEL REPOSITORIO REMOTO
echo ===================================================================
echo.
echo Ve a tu GitHub (https://github.com/new) y copia la URL de tu repositorio.
echo Ejemplo: https://github.com/Ed11jo78/korelife.git
echo.
set /p REPO_URL="Pega la URL de tu repositorio de GitHub: "

if not "%REPO_URL%"=="" (
    echo.
    echo Configurando origen remoto...
    git remote remove origin >nul 2>nul
    git remote add origin %REPO_URL%
    echo.
    echo Subiendo proyecto a GitHub...
    git push -u origin main --force
    echo.
    echo ===================================================================
    echo  LISTO! Tu proyecto KoreLife se ha subido exitosamente a GitHub.
    echo ===================================================================
) else (
    echo No ingresaste URL. Puedes hacer el push manualmente con:
    echo   git remote add origin TU_URL_DE_GITHUB
    echo   git push -u origin main
)

pause
