@echo off
setlocal EnableExtensions
set "ROOT=%~dp0.."
cd /d "%ROOT%"
if "%ROOT:~-1%"=="\" set "ROOT=%ROOT:~0,-1%"
for %%I in ("%ROOT%") do set "ROOT=%%~fI"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0download-deps.ps1" -ProjectRoot "%ROOT%"
exit /b %ERRORLEVEL%
