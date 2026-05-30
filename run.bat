@echo off
setlocal EnableExtensions
cd /d "%~dp0"

call "%~dp0compile-main.bat"
if errorlevel 1 exit /b 1

call "%~dp0lib-runtime-classpath.bat"
if errorlevel 1 exit /b 1

set /p RUN_CP=<build\runtime-cp.txt
chcp 65001 >nul
java -Dfile.encoding=UTF-8 -cp "build\classes;%RUN_CP%" org.example.MainKt
