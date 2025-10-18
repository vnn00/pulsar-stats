#!/bin/bash

# Pulsar Stats - Linux Build Script

echo "╔════════════════════════════════════════════════════════╗"
echo "║    Pulsar Stats - Building for Linux x64              ║"
echo "╚════════════════════════════════════════════════════════╝"

cd SystemMonitorServer

echo "[1/4] Restoring NuGet packages..."
dotnet restore

echo "[2/4] Building project..."
dotnet build -c Release

echo "[3/4] Publishing self-contained executable..."
dotnet publish -c Release -r linux-x64 --self-contained true -p:PublishSingleFile=true -p:PublishTrimmed=true

echo "[4/4] Setting executable permissions..."
chmod +x bin/Release/net9.0/linux-x64/publish/SystemMonitorServer

echo ""
echo "✅ Build complete!"
echo "Output: SystemMonitorServer/bin/Release/net9.0/linux-x64/publish/SystemMonitorServer"
echo ""
echo "To run:"
echo "  cd SystemMonitorServer/bin/Release/net9.0/linux-x64/publish"
echo "  ./SystemMonitorServer"
