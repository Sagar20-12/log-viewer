# PowerShell script to add timestamps to log.txt and maintain only last 10 entries
# Run this script to simulate log entries

$logFile = "log.txt"

# Create log file if it doesn't exist
if (!(Test-Path $logFile)) {
    New-Item -ItemType File -Name $logFile
}

Write-Host "Starting log generation. Press Ctrl+C to stop."
Write-Host "Log file: $logFile"

try {
    for ($i = 1; $i -le 1000; $i++) {
        # Get current timestamp
        $timestamp = Get-Date -Format "ddd MMM dd HH:mm:ss yyyy"
        
        # Add timestamp to log file
        Add-Content -Path $logFile -Value $timestamp
        
        # Read all lines and keep only the last 10
        $lines = Get-Content -Path $logFile
        if ($lines.Count -gt 10) {
            $last10Lines = $lines | Select-Object -Last 10
            Set-Content -Path $logFile -Value $last10Lines
        }
        
        Write-Host "Added timestamp: $timestamp (Entry $i)"
        Start-Sleep -Seconds 1
    }
} catch {
    Write-Host "`nScript stopped by user or error occurred."
}

