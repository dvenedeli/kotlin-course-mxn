@echo off
setlocal EnableExtensions EnableDelayedExpansion
set "ROOT=%~dp0.."
cd /d "%ROOT%"

set "CP="
for %%F in ("lib\*.jar") do (
  set "NAME=%%~nxF"
  echo !NAME! | findstr /i /r "serialization-compiler-plugin" >nul
  if errorlevel 1 (
    if defined CP (
      set "CP=!CP!;%%~fF"
    ) else (
      set "CP=%%~fF"
    )
  )
)
if not defined CP (
  echo No test compile classpath jars found in lib\
  exit /b 1
)

if not exist "build" mkdir "build"
> build\test-cp.txt echo !CP!
exit /b 0
