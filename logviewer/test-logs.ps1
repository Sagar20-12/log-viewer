# Test script to add initial log entries
$logFile = "log.txt"

# Clear existing content and add some test entries
$testEntries = @(
    "Sat May 13 10:48:49 IST 2023",
    "Sat May 13 10:48:50 IST 2023",
    "Sat May 13 10:48:51 IST 2023",
    "Sat May 13 10:48:52 IST 2023",
    "Sat May 13 10:48:53 IST 2023"
)

Set-Content -Path $logFile -Value $testEntries

Write-Host "Added $($testEntries.Count) test entries to $logFile" -ForegroundColor Green
Write-Host "Content of log.txt:" -ForegroundColor Yellow
Get-Content -Path $logFile | ForEach-Object { Write-Host "  $_" }

