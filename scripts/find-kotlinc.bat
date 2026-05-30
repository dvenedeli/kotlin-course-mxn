@echo off
setlocal EnableExtensions
set "ROOT=%~dp0.."
cd /d "%ROOT%"

set "KOTLINC=kotlinc"
where kotlinc >nul 2>&1
if not errorlevel 1 goto :write

if defined KOTLIN_HOME (
  if exist "%KOTLIN_HOME%\bin\kotlinc.bat" set "KOTLINC=%KOTLIN_HOME%\bin\kotlinc.bat" & goto :write
  if exist "%KOTLIN_HOME%\bin\kotlinc" set "KOTLINC=%KOTLIN_HOME%\bin\kotlinc" & goto :write
)

if exist "tools\kotlin-1.9.24\bin\kotlinc.bat" (
  set "KOTLINC=%ROOT%\tools\kotlin-1.9.24\bin\kotlinc.bat"
  goto :write
)

echo kotlinc not found. Install Kotlin 1.9.x and add it to PATH, or set KOTLIN_HOME.
echo Run download-deps.bat to install Kotlin compiler into tools\kotlin-1.9.24\
exit /b 1

:write
if not exist "build" mkdir "build"
> build\kotlinc.txt echo %KOTLINC%
exit /b 0
