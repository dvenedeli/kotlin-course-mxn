@echo off
cd /d "%~dp0"
call "%~dp0scripts\download-deps.bat"
exit /b %ERRORLEVEL%
