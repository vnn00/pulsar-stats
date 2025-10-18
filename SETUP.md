# Setup Guide for Developers

This guide will help you set up the Pulsar Stats project for development.

## Prerequisites

### For Server Development (.NET)
- [.NET 9.0 SDK](https://dotnet.microsoft.com/download/dotnet/9.0) or later
- Visual Studio 2022 / VS Code / Rider (optional)
- Windows or Linux

### For Android Development
- [JDK 21 (LTS)](https://learn.microsoft.com/en-us/java/openjdk/download)
- [Android Studio](https://developer.android.com/studio) (recommended) or Android SDK command-line tools
- Android SDK API 34
- Gradle 8.13 (included via wrapper)

## Setup Steps

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/pulsar-stats.git
cd pulsar-stats
```

### 2. Server Setup (Windows/Linux)

#### Windows
```powershell
cd PulsarStatsServer

# Restore dependencies
dotnet restore

# Build the project
dotnet build

# Run (requires Administrator for temperature monitoring)
# Right-click Command Prompt or PowerShell > Run as Administrator
dotnet run
```

The server will start on `http://localhost:5066`

#### Linux
```bash
cd PulsarStatsServer

# Install lm-sensors for temperature monitoring
sudo apt-get install lm-sensors  # Debian/Ubuntu
sudo dnf install lm_sensors       # Fedora/RHEL

# Restore dependencies
dotnet restore

# Build the project
dotnet build

# Run (requires sudo for sensor access)
sudo dotnet run
```

### 3. Android Setup

#### Step 1: Configure Android SDK

Create `PulsarStatsMobile/local.properties` file:

```properties
# Windows example
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

# macOS example
# sdk.dir=/Users/YourUsername/Library/Android/sdk

# Linux example
# sdk.dir=/home/YourUsername/Android/Sdk
```

Or copy the example file:
```bash
cd PulsarStatsMobile
cp local.properties.example local.properties
# Edit local.properties with your Android SDK path
```

#### Step 2: Configure JDK 21 (if needed)

Edit `PulsarStatsMobile/gradle.properties` to set your JDK 21 path:

```properties
org.gradle.java.home=C:/Program Files/Java/jdk-21
```

Or let Gradle use your JAVA_HOME environment variable (if set to JDK 21).

#### Step 3: Build the App

**Using Android Studio:**
1. Open Android Studio
2. File > Open > Select `PulsarStatsMobile` folder
3. Wait for Gradle sync to complete
4. Build > Make Project
5. Run > Run 'app'

**Using Command Line:**

```bash
cd PulsarStatsMobile

# Build debug APK
./gradlew assembleDebug    # Linux/macOS
.\gradlew.bat assembleDebug   # Windows

# The APK will be at: app/build/outputs/apk/debug/app-debug.apk

# Install to connected device
./gradlew installDebug
```

### 4. Creating a Release Build

#### Step 1: Generate a Keystore

```bash
cd PulsarStatsMobile/app

# Generate keystore
keytool -genkey -v -keystore my-release-key.keystore \
  -alias my-key-alias \
  -keyalg RSA -keysize 2048 \
  -validity 10000

# Follow the prompts to set passwords and details
```

#### Step 2: Configure Signing

Edit `app/build.gradle` to add your signing config:

```gradle
android {
    signingConfigs {
        release {
            storeFile file('my-release-key.keystore')
            storePassword 'your-keystore-password'
            keyAlias 'my-key-alias'
            keyPassword 'your-key-password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

**⚠️ IMPORTANT**: Never commit your keystore or passwords to version control!

Add to `.gitignore`:
```
*.keystore
*.jks
```

#### Step 3: Build Release APK

```bash
./gradlew assembleRelease

# The APK will be at: app/build/outputs/apk/release/app-release.apk
```

## Testing

### Server
```bash
cd PulsarStatsServer
dotnet test
```

### Android
```bash
cd PulsarStatsMobile
./gradlew test
./gradlew connectedAndroidTest  # Requires connected device/emulator
```

## Common Issues

### Issue: "SDK location not found"
**Solution**: Create `local.properties` with your Android SDK path (see Step 3.1)

### Issue: "Java version incompatible"
**Solution**: Install JDK 21 and configure `gradle.properties` or JAVA_HOME

### Issue: "Temperature sensors not working"
**Solution (Windows)**: Run the server as Administrator
**Solution (Linux)**: Run with sudo and ensure lm-sensors is installed

### Issue: Gradle build fails
**Solution**: 
```bash
cd PulsarStatsMobile
./gradlew clean build --refresh-dependencies
```

### Issue: Android Studio Gradle sync fails
**Solution**:
1. File > Invalidate Caches / Restart
2. Check that JDK 21 is configured in Project Structure
3. Verify `local.properties` has correct SDK path

## Environment Variables

### Optional: Set JAVA_HOME (if not using gradle.properties)
```bash
# Windows
setx JAVA_HOME "C:\Program Files\Java\jdk-21"

# Linux/macOS
export JAVA_HOME=/usr/lib/jvm/jdk-21
```

## Development Tips

1. **Hot Reload (Server)**: Use `dotnet watch run` for automatic restarts on code changes

2. **Android Debugging**: Enable Developer Options and USB Debugging on your device

3. **Network Testing**: Use Android emulator's `10.0.2.2` to access localhost on host machine

4. **Logs**: 
   - Server: Console output or configure logging in `appsettings.json`
   - Android: Use Logcat in Android Studio or `adb logcat`

## Need Help?

- Check [README.md](README.md) for general information
- Review [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines
- Open an issue on GitHub for bugs or questions

---

Happy coding! 🚀
