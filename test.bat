@echo off
setlocal EnableExtensions
cd /d "%~dp0"

call "%~dp0scripts\compile-main.bat"
if errorlevel 1 exit /b 1

call "%~dp0scripts\find-kotlinc.bat"
if errorlevel 1 exit /b 1

call "%~dp0scripts\lib-test-classpath.bat"
if errorlevel 1 exit /b 1

set /p KOTLINC=<build\kotlinc.txt
set /p TEST_LIB_CP=<build\test-cp.txt

set "PLUGIN=lib\kotlinx-serialization-compiler-plugin-1.9.24.jar"
set "OUT=build\test-classes"
set "TEST_CP=build\classes;%TEST_LIB_CP%"

if not exist "%OUT%" mkdir "%OUT%"

dir /s /b test\org\example\*.kt > build\sources-test.txt
if errorlevel 1 (
  echo No Kotlin test sources found under test\org\example
  exit /b 1
)

"%KOTLINC%" -jvm-target 11 -cp "%TEST_CP%" -Xplugin=%PLUGIN% -d "%OUT%" @build\sources-test.txt
if errorlevel 1 exit /b 1

java -jar lib\junit-platform-console-standalone-1.10.2.jar --class-path "build\classes;build\test-classes;%TEST_LIB_CP%" --scan-class-path
exit /b %ERRORLEVEL%
