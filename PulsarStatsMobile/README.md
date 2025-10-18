# System Monitor Mobile (Android)

Native Android application - PC system monitoring app.

## Features

✅ **Real-time Monitoring**
- CPU usage and frequency
- RAM usage (used/total)
- Disk usage (all drives)
- Temperatures (CPU, GPU, Motherboard)
- Network speeds (download/upload)

✅ **Smart Notifications**
- Configurable threshold values
- CPU usage notifications (50-100%)
- RAM usage notifications (50-100%)
- CPU temperature notifications (60-100°C)
- GPU temperature notifications (60-100°C)
- Disk temperature notifications (40-80°C)
- Sound and vibration settings

✅ **User Friendly**
- Material Design 3 interface
- Swipe-to-refresh
- Save server IP address
- Auto-reconnect

## Requirements

### Development
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 21 (LTS)
- Android SDK (API 24-34)
- Gradle 8.13 (included via wrapper)

### Device
- Android 7.0 (API 24) or newer
- Internet/WiFi connection (LAN)

## Installation & Build

### 1. Open Project
```bash
# In Android Studio: File > Open > Select SystemMonitorMobile folder
```

### 2. Gradle Sync
Android Studio will automatically download dependencies.

### 3. APK Build

#### Debug APK (For Testing)
```bash
# In terminal:
cd SystemMonitorMobile
gradlew assembleDebug

# APK location: app\build\outputs\apk\debug\app-debug.apk
```

#### Release APK (For Publishing)
```bash
gradlew assembleRelease

# APK location: app\build\outputs\apk\release\app-release-unsigned.apk
```

### 4. Install on Device

**Via USB:**
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

**Manual:**
1. Copy APK file to phone
2. Open APK from file manager
3. Allow "Install from unknown sources"
4. Install

## Usage

### First Run

1. **Start Server**
   - Run `SystemMonitorServer.exe` on PC
   - Note the server's IP address (e.g., `192.168.1.100`)

2. **Open Mobile App**
   - Enter IP address (e.g., `192.168.1.100`)
   - Port: `5000` (default)
   - Tap "Connect" button

3. **Start Monitoring**
   - Main screen opens when connection is successful
   - All metrics update every 3 seconds

### Settings

**Configure Notification Thresholds:**
1. Tap ⚙️ icon in top-right corner
2. For each metric:
   - Enable/disable notification
   - Set threshold value (with slider)
3. Select sound/vibration preferences
4. Tap "Save" button

**Change Server:**
1. Settings > "Change Server Address"
2. Enter new IP address

### Features

- **Swipe Down**: Refresh connection
- **⚙️ Icon**: Open settings screen
- **🔄 Icon**: Manual refresh

## Troubleshooting

### Connection Error

**"Connection failed!"**
- ✅ Are PC and phone connected to same WiFi?
- ✅ Is server running?
- ✅ Is IP address correct?
- ✅ Is port 5000 open?
- ✅ Is Windows Firewall blocking the server?

**Finding IP Address (on PC):**
```powershell
ipconfig
# Look for "IPv4 Address" line (e.g., 192.168.1.100)
```

### Notifications Not Working

- ✅ Settings > Are notifications enabled?
- ✅ Are app notifications enabled in Android system settings?
- ✅ Are threshold values too high?
- ✅ Is battery saver mode off?

### Running App in Background

Android 12+:
1. Settings > Apps > System Monitor
2. Battery > Unrestricted
3. Notifications > Allow all notifications

## Technical Details

### Technologies Used
- **Language**: Java
- **UI**: Material Design 3
- **WebSocket**: SignalR Java Client 7.0
- **JSON**: Gson 2.10.1
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Architecture
```
App
├── Activities
│   ├── SplashActivity (IP entry)
│   ├── MainActivity (monitoring)
│   └── SettingsActivity (settings)
├── Services
│   └── MonitoringService (background)
├── Models
│   ├── SystemData
│   ├── CpuInfo, MemoryInfo, etc.
│   └── NotificationSettings
└── Utils
    ├── SignalRManager (WebSocket)
    └── PreferencesHelper (storage)
```

## Development Notes

### Updating Dependencies
```gradle
// app/build.gradle
dependencies {
    implementation 'com.microsoft.signalr:signalr:7.0.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.google.android.material:material:1.11.0'
    // ...
}
```

### Build Variants
- **Debug**: For development, no ProGuard
- **Release**: For publishing, ProGuard enabled

## License
See [LICENSE](../../LICENSE) file for details.

## Contributors
- vnn00 (Developer)

## Version History

### v3.10.4 (October 18, 2025)
- ✅ Java 21 LTS support
- ✅ Real-time monitoring
- ✅ Notification system
- ✅ Configurable thresholds
- ✅ Material Design 3 UI

## Contact
For questions, please use GitHub Issues.

---

**Note:** This application is designed to work on the same WiFi network as the PC. For internet access, port forwarding and security settings are required.
