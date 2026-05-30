@echo off
setlocal EnableExtensions
set "ROOT=%~dp0.."
cd /d "%ROOT%"

call "%~dp0ensure-deps.bat"
if errorlevel 1 exit /b 1

call "%~dp0find-kotlinc.bat"
if errorlevel 1 exit /b 1

call "%~dp0lib-classpath.bat"
if errorlevel 1 exit /b 1

set /p KOTLINC=<build\kotlinc.txt
set /p LIB_CP=<build\lib-cp.txt

set "PLUGIN=lib\kotlinx-serialization-compiler-plugin-1.9.24.jar"
set "OUT=build\classes"

if not exist "build" mkdir "build"
if not exist "%OUT%" mkdir "%OUT%"

dir /s /b src\org\example\*.kt > build\sources-main.txt
if errorlevel 1 (
  echo No Kotlin sources found under src\org\example
  exit /b 1
)

"%KOTLINC%" -jvm-target 11 -cp "%LIB_CP%" -Xplugin=%PLUGIN% -d "%OUT%" @build\sources-main.txt
exit /b %ERRORLEVEL%
