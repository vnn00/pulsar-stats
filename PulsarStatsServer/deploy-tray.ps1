# Deploy System Monitor Server v2.0 to Release folder

Write-Host "================================================" -ForegroundColor Cyan
Write-Host " System Monitor Server v2.0 - Deployment" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$publishDir = ".\publish"
$releaseDir = "..\Release"

# Check if publish folder exists
if (!(Test-Path $publishDir)) {
    Write-Host "❌ Publish folder not found!" -ForegroundColor Red
    Write-Host "   Please run: dotnet publish -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true -o .\publish" -ForegroundColor Yellow
    exit 1
}

Write-Host "📦 Copying files to Release folder..." -ForegroundColor Green

# Copy EXE
Copy-Item "$publishDir\SystemMonitorServer.exe" $releaseDir -Force
Write-Host "✅ SystemMonitorServer.exe copied" -ForegroundColor Green

# Copy README
Copy-Item ".\README-TRAY.md" "$releaseDir\README-TRAY-v2.md" -Force
Write-Host "✅ README-TRAY-v2.md copied" -ForegroundColor Green

# Copy test-client if exists
if (Test-Path ".\test-client.html") {
    Copy-Item ".\test-client.html" $releaseDir -Force
    Write-Host "✅ test-client.html copied" -ForegroundColor Green
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " ✅ Deployment Complete!" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Get EXE size
$exeFile = Get-Item "$releaseDir\SystemMonitorServer.exe"
$sizeMB = [math]::Round($exeFile.Length / 1MB, 2)

Write-Host "📊 File Information:" -ForegroundColor Yellow
Write-Host "   Location: $releaseDir" -ForegroundColor White
Write-Host "   Size: $sizeMB MB" -ForegroundColor White
Write-Host ""
Write-Host "🚀 Ready to use! Run SystemMonitorServer.exe" -ForegroundColor Green
Write-Host "   - Will appear in system tray (no console window)" -ForegroundColor Cyan
Write-Host "   - Right-click for menu" -ForegroundColor Cyan
Write-Host "   - Double-click for server info" -ForegroundColor Cyan
Write-Host ""
