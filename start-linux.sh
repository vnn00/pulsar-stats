#!/bin/bash

# Pulsar Stats - Linux Run Script

echo "╔════════════════════════════════════════════════════════╗"
echo "║    Pulsar Stats Server - Starting on Linux            ║"
echo "╚════════════════════════════════════════════════════════╝"

cd SystemMonitorServer

# Check if built
if [ ! -f "bin/Release/net9.0/linux-x64/publish/SystemMonitorServer" ]; then
    echo "❌ Build not found. Please run build-linux.sh first."
    exit 1
fi

echo "Starting server..."
echo ""

cd bin/Release/net9.0/linux-x64/publish
./SystemMonitorServer
