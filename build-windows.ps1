# Pulsar Stats - Windows Build Script

Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║    Pulsar Stats - Building for Windows x64            ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan

Set-Location SystemMonitorServer

Write-Host "[1/3] Restoring NuGet packages..." -ForegroundColor Yellow
dotnet restore

Write-Host "[2/3] Building project..." -ForegroundColor Yellow
dotnet build -c Release

Write-Host "[3/3] Publishing self-contained executable..." -ForegroundColor Yellow
dotnet publish -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true -p:PublishTrimmed=true

Write-Host ""
Write-Host "✅ Build complete!" -ForegroundColor Green
Write-Host "Output: SystemMonitorServer\bin\Release\net9.0\win-x64\publish\SystemMonitorServer.exe" -ForegroundColor White
Write-Host ""
Write-Host "To run: Right-click SystemMonitorServer.exe -> Run as administrator" -ForegroundColor Cyan

Set-Location ..
