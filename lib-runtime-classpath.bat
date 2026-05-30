@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "CP="
for %%F in ("lib\*.jar") do (
  set "NAME=%%~nxF"
  echo !NAME! | findstr /i /r "serialization-compiler-plugin junit" >nul
  if errorlevel 1 (
    if defined CP (
      set "CP=!CP!;%%~fF"
    ) else (
      set "CP=%%~fF"
    )
  )
)
if not defined CP (
  echo No runtime classpath jars found in lib\
  exit /b 1
)

if not exist "build" mkdir "build"
> build\runtime-cp.txt echo !CP!
exit /b 0
