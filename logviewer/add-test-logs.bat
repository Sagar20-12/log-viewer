@echo off
echo Starting log generation script...
echo This will add timestamps to log.txt every second
echo Press Ctrl+C to stop the script
echo.

powershell -ExecutionPolicy Bypass -File "add-test-logs.ps1"

pause

