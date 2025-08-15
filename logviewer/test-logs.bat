@echo off
echo Adding test log entries...
echo %date% %time% - Application started >> log.txt
timeout /t 2 /nobreak >nul
echo %date% %time% - User login successful >> log.txt
timeout /t 2 /nobreak >nul
echo %date% %time% - Database connection established >> log.txt
timeout /t 2 /nobreak >nul
echo %date% %time% - Processing request ID: 12345 >> log.txt
timeout /t 2 /nobreak >nul
echo %date% %time% - Request completed successfully >> log.txt
echo Test log entries added successfully!
pause

