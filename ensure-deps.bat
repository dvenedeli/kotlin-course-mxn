@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if exist "lib\junit-platform-console-standalone-1.10.2.jar" if exist "lib\kotlinx-serialization-compiler-plugin-1.9.24.jar" exit /b 0

echo Fetching dependencies (first run may take a minute)...
call "%~dp0download-deps.bat"
exit /b %ERRORLEVEL%
