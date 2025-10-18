# System Monitor Server v2.0 - System Tray Edition

## 🎯 What's New (v2.0)

### ✨ System Tray Support
- Application now runs in Windows system tray
- Silent background operation without console
- Easy access via right-click menu

### ⚙️ New Features

#### 1. Right-Click Menu
- **📊 Server Info**: Server status and connection information
- **🌐 Open Test Page**: Opens test client in web browser
- **📝 Show Logs**: (Coming in future release)

#### 2. Settings Menu
- **Start with Windows**: Auto-start when computer boots
- **Run in Stealth Mode**: Hide completely, even from system tray

#### 3. Stealth Mode Feature
- System tray icon becomes invisible when activated
- Remains hidden even after Windows restart
- Setting saved in Windows Registry
- To disable: Terminate from Task Manager and restart application to disable setting

## 🚀 Usage

### First Launch
1. Run `SystemMonitorServer.exe`
2. Application icon will appear in system tray (next to clock)
3. Right-click the icon to see the menu

### View Server Information
- **Double-click** the icon or
- Right-click → **Server Info**

### Auto-Start Setup
1. Right-click → **Settings** → **Start with Windows**
2. Check this option to enable auto-start
3. Server will now start automatically when Windows boots

### Stealth Mode
⚠️ **WARNING**: Read carefully before enabling this mode!

1. Right-click → **Settings** → **Run in Stealth Mode**
2. Read confirmation message and click "OK"
3. System tray icon will now be **invisible**
4. Server continues running in background

**To Disable Stealth Mode**:
1. Open Task Manager (Ctrl+Shift+Esc)
2. Find "SystemMonitorServer.exe" process and terminate it
3. Restart application normally
4. Right-click → Settings → Uncheck "Run in Stealth Mode"

### Closing the Application
- Right-click → **Exit**
- Confirm with "Yes" in the dialog

## 📡 Connection Information

- **URL**: `http://[COMPUTER_IP]:5000`
- **SignalR Hub**: `/systemhub`
- **Update Interval**: 3 seconds
- **Platform**: Windows (.NET 9.0)

## 🔧 Technical Details

### Registry Locations

**Auto-Start**:
```
HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Run
Key: SystemMonitor
Value: "C:\...\SystemMonitorServer.exe"
```

**Stealth Mode Setting**:
```
HKEY_CURRENT_USER\SOFTWARE\SystemMonitor
Key: HideOnStartup
Value: 1 (hidden) / 0 (visible)
```

### Features
- **Self-contained**: .NET runtime included, no installation required
- **Single-file**: Single EXE file
- **Admin rights**: Administrator privileges recommended for temperature sensors
- **Windows Forms**: Native Windows UI
- **ASP.NET Core**: Web API and SignalR backend

## 📂 File Size

- **EXE**: ~100-105 MB (self-contained, all dependencies included)
- **Runtime**: .NET 9.0 Windows

## 🐛 Troubleshooting

### Icon not visible in system tray
- Check if "Stealth Mode" is enabled in Settings
- Check if application is running in Task Manager

### Not starting with Windows
- Run as administrator and re-enable the setting
- Manually check `Run` key in Registry

### No temperature data
- **Run as administrator**
- Your hardware may not support temperature sensors

### Android app can't connect
- Check if port 5000 is open in Windows Firewall
- Make sure you're on the same WiFi network
- Verify the IP address is correct

## 📝 Changelog

### v2.0.0 (October 12, 2025)
- ✨ Added system tray support
- ✨ Windows auto-start feature
- ✨ Stealth mode feature
- ✨ Easy management with right-click menu
- 🔧 Console-free operation (WinExe)
- 🔧 Registry-based settings storage

### v1.1.0 (October 12, 2025)
- 🐛 Fixed network binding (0.0.0.0:5000)
- 🐛 Fixed JSON serialization (camelCase)
- 📱 Compatibility with Android app

### v1.0.0 (October 12, 2025)
- 🎉 Initial release
- ✅ CPU, RAM, Disk, Temperature, Network monitoring
- ✅ SignalR WebSocket support
- ✅ Self-contained EXE

## 📞 Support

Use GitHub Issues for problems or suggestions.

---

**© 2025 System Monitor Server**  
Developed with ❤️ using .NET 9.0
