@echo off
echo Building HomePlugin...
cd /d "%~dp0"
call mvn clean package
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! Plugin JAR is in target folder.
    echo Copying to plugins folder...
    copy /Y "target\HomePlugin-1.0.0.jar" "..\plugins\HomePlugin.jar"
    echo Done!
) else (
    echo Build failed!
)
pause
