# 🌟 Pulsar Stats

[![.NET](https://img.shields.io/badge/.NET-9.0-512BD4?logo=dotnet)](https://dotnet.microsoft.com/)
[![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?logo=android)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux-blue)]()

Real-time system monitoring solution for Windows and Linux with Android client. Monitor your PC's CPU, RAM, GPU, temperatures, disk usage, and network activity from anywhere on your local network.

## 📱 Features

### Server (Windows/Linux)
- **Real-time Hardware Monitoring**
  - CPU usage, frequency, and temperature
  - RAM usage and availability
  - GPU usage, VRAM, and temperature
  - Disk usage for all drives
  - Network traffic (upload/download)
  - Motherboard and storage temperatures

- **Silent Screenshot Capture**
  - Take screenshots remotely without interrupting gameplay
  - No notifications or UI popups
  - Instant delivery to Android client

- **System Tray Integration** (Windows)
  - Background operation
  - Minimal resource usage (~20MB RAM)
  - Auto-start with system (optional)

### Android Client
- **Live Data Display**
  - Real-time metrics updated every 1-3 seconds
  - Clean Material Design 3 interface
  - Dark/Light theme support

- **Background Service**
  - Persistent notification with live stats
  - Continuous monitoring even when app is closed
  - Auto-reconnect on network changes

- **Remote Screenshot**
  - One-tap screenshot capture
  - Fullscreen viewer with 30s auto-dismiss
  - No server interruption

- **Customizable Settings**
  - Update interval (1-10 seconds)
  - Alert thresholds (CPU, RAM, temperature)
  - Language support (English/Turkish)
  - Auto-start on boot

## 🚀 Quick Start

### Server Setup (Windows)

1. **Download the latest release**
   ```
   Download: SystemMonitorServer-v3.10.3.sys (136 MB)
   ```

2. **Run as Administrator** (required for temperature sensors)
   ```cmd
   SystemMonitorServer.exe
   ```

3. **Server will start on port 5066**
   ```
   Server URL: http://localhost:5066
   SignalR Hub: /systemMonitorHub
   ```

### Server Setup (Linux)

See [Linux Port Documentation](#-linux-port) below.

### Android Setup

1. **Download and install APK**
   ```
   Download: PulsarStats-v3.10.4.apk (5.6 MB)
   ```

2. **Open app and enter server IP**
   ```
   Example: 192.168.1.100:5066
   ```

3. **Grant required permissions**
   - Notification permission
   - Battery optimization exclusion (for background service)

4. **Tap Connect** and enjoy real-time monitoring!

## 📊 Screenshots

### Android Client
```
┌────────────────────────────────┐
│  Pulsar Stats                  │
│  192.168.1.100:5066  [●]      │
├────────────────────────────────┤
│  CPU: 45%  │  RAM: 12.4GB     │
│  GPU: 62°C │  VRAM: 4.2GB     │
├────────────────────────────────┤
│  System Information            │
│  Computer: DESKTOP-PC          │
│  OS: Windows 11 Pro            │
│  Uptime: 2d 14h 32m            │
├────────────────────────────────┤
│  [📸 Take Screenshot]          │
└────────────────────────────────┘
```

## 🛠️ Building from Source

### Prerequisites
- .NET 9.0 SDK
- Android Studio (for Android client)
- Java 17 JDK
- Gradle 8.7+

### Server Build (Windows)

```powershell
cd SystemMonitorServer
dotnet restore
dotnet publish -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true
```

Output: `bin/Release/net9.0/win-x64/publish/SystemMonitorServer.exe`

### Server Build (Linux)

```bash
cd SystemMonitorServer
dotnet restore
dotnet publish -c Release -r linux-x64 --self-contained true -p:PublishSingleFile=true
```

Output: `bin/Release/net9.0/linux-x64/publish/SystemMonitorServer`

### Android Build

```bash
cd SystemMonitorMobile
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

To sign the APK, use Android Studio or `apksigner` tool.

## 🐧 Linux Port

Pulsar Stats server now supports Linux! The following sensors are available on Linux:

### Supported on Linux
- ✅ CPU usage and frequency
- ✅ RAM usage
- ✅ Disk usage
- ✅ Network traffic
- ✅ System information

### Platform-Specific Features
- ⚠️ Hardware temperatures (requires `lm-sensors` and appropriate drivers)
- ⚠️ GPU monitoring (limited support, depends on hardware)

### Linux Installation

1. **Install .NET 9.0 Runtime**
   ```bash
   wget https://dot.net/v1/dotnet-install.sh
   chmod +x dotnet-install.sh
   ./dotnet-install.sh --channel 9.0
   ```

2. **Install lm-sensors** (for temperature monitoring)
   ```bash
   # Ubuntu/Debian
   sudo apt install lm-sensors
   sudo sensors-detect
   
   # Arch Linux
   sudo pacman -S lm_sensors
   
   # Fedora
   sudo dnf install lm_sensors
   ```

3. **Run the server**
   ```bash
   chmod +x SystemMonitorServer
   ./SystemMonitorServer
   ```

4. **Optional: Create systemd service**
   ```bash
   sudo nano /etc/systemd/system/pulsarstats.service
   ```
   
   Add:
   ```ini
   [Unit]
   Description=Pulsar Stats Monitoring Server
   After=network.target
   
   [Service]
   Type=simple
   User=yourusername
   WorkingDirectory=/opt/pulsarstats
   ExecStart=/opt/pulsarstats/SystemMonitorServer
   Restart=always
   
   [Install]
   WantedBy=multi-user.target
   ```
   
   Enable and start:
   ```bash
   sudo systemctl enable pulsarstats
   sudo systemctl start pulsarstats
   ```

## ⚙️ Configuration

Server settings can be configured in `appsettings.json`:

```json
{
  "Monitoring": {
    "UpdateIntervalSeconds": 3,
    "EnableTemperatureSensors": true,
    "EnableGpuMonitoring": true
  },
  "Kestrel": {
    "Endpoints": {
      "Http": {
        "Url": "http://0.0.0.0:5066"
      }
    }
  }
}
```

## 📦 Tech Stack

### Server
- .NET 9.0 (C#)
- ASP.NET Core SignalR
- LibreHardwareMonitor (sensor library)
- System.Diagnostics.PerformanceCounter

### Android
- Java 21 (LTS)
- Android SDK 34
- Gradle 8.13
- Material Design 3
- Microsoft SignalR Java Client
- Gson (JSON parsing)

### Website
- HTML5/CSS3/JavaScript
- Modern CSS Grid/Flexbox
- Intersection Observer API
- Material Design inspired

## 🛠️ Building from Source

### Prerequisites

#### Server (Windows/Linux)
- .NET 9.0 SDK or later
- Windows: Administrator privileges for temperature monitoring
- Linux: lm-sensors package for temperature monitoring

#### Android
- JDK 21 (LTS)
- Android SDK 34
- Gradle 8.13 (included via wrapper)

### Build Instructions

#### Server
```powershell
# Windows
cd PulsarStatsServer
dotnet build
dotnet run

# Linux
cd PulsarStatsServer
dotnet build
sudo dotnet run  # sudo needed for sensor access
```

#### Android
```bash
cd PulsarStatsMobile

# Create local.properties with your Android SDK path
echo "sdk.dir=/path/to/your/Android/Sdk" > local.properties

# For release builds, create your own keystore
keytool -genkey -v -keystore app/release.keystore -alias mykey -keyalg RSA -keysize 2048 -validity 10000

# Build debug APK
./gradlew assembleDebug

# Build release APK (after configuring signing)
./gradlew assembleRelease
```

**Note**: For security reasons, the original signing keystore is not included in this repository. You must create your own keystore for release builds.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Workflow

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🐛 Known Issues

- Temperature sensors require administrator privileges on Windows
- GPU monitoring may not work with all graphics cards
- Linux temperature sensors depend on hardware support and drivers
- Screenshot feature is Windows-only (Linux support planned)

## 🗺️ Roadmap

- [ ] Linux screenshot support
- [ ] macOS server support
- [ ] iOS client
- [ ] Web-based monitoring dashboard
- [ ] Historical data logging and charts
- [ ] Multiple PC monitoring from single client
- [ ] Custom alert notifications
- [ ] RESTful API for third-party integrations

## 📧 Contact

For questions, suggestions, or bug reports, please open an issue on GitHub.

## ⭐ Show Your Support

If you find this project useful, please consider giving it a star on GitHub!

---

**Made with ❤️ for PC enthusiasts and gamers**
