# Android APK Build Script (Without Gradle Wrapper)
# This script uses Android SDK directly to build APK

Write-Host "=== Android APK Build Script ===" -ForegroundColor Cyan
Write-Host ""

# Set Android SDK path
$env:ANDROID_HOME = "C:\Users\basgu\AppData\Local\Android\Sdk"
Write-Host "✓ ANDROID_HOME: $env:ANDROID_HOME" -ForegroundColor Green

# Check if Android SDK exists
if (-not (Test-Path $env:ANDROID_HOME)) {
    Write-Host "✗ Android SDK not found!" -ForegroundColor Red
    exit 1
}

# Set Java path (from Android Studio's bundled JDK)
$javaPath = "$env:ANDROID_HOME\..\Android Studio\jbr\bin\java.exe"
if (Test-Path $javaPath) {
    $env:JAVA_HOME = "$env:ANDROID_HOME\..\Android Studio\jbr"
    Write-Host "✓ JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
} else {
    # Try system Java
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        Write-Host "✓ Using system Java: $javaVersion" -ForegroundColor Green
    } catch {
        Write-Host "✗ Java not found!" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "=== Building APK ===" -ForegroundColor Cyan

# Navigate to project directory
$projectDir = "C:\Users\basgu\OneDrive\Resimler\appTest\SystemMonitorMobile"
Set-Location $projectDir

# Method 1: Use Gradle from Android Studio if available
$gradlePath = "$env:ANDROID_HOME\..\Android Studio\plugins\gradle\lib\gradle.jar"
if (Test-Path $gradlePath) {
    Write-Host "✓ Found Gradle in Android Studio" -ForegroundColor Green
    # This won't work directly, need gradlew
}

# Method 2: Install Gradle wrapper first
Write-Host "Installing Gradle wrapper..." -ForegroundColor Yellow

# Create gradle wrapper properties
$wrapperDir = "gradle\wrapper"
if (-not (Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Force -Path $wrapperDir | Out-Null
}

# Download gradle-wrapper.jar
$wrapperJarUrl = "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
$wrapperJarPath = "$wrapperDir\gradle-wrapper.jar"

Write-Host "Downloading Gradle wrapper..." -ForegroundColor Yellow
try {
    Invoke-WebRequest -Uri $wrapperJarUrl -OutFile $wrapperJarPath -UseBasicParsing
    Write-Host "✓ Gradle wrapper downloaded" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to download Gradle wrapper: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "=== ALTERNATIVE: Use Android Studio ===" -ForegroundColor Yellow
    Write-Host "1. Open project in Android Studio"
    Write-Host "2. Build > Build Bundle(s) / APK(s) > Build APK(s)"
    Write-Host "3. APK will be in: app\build\outputs\apk\debug\"
    exit 1
}

# Create gradle-wrapper.properties
$wrapperProps = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@

Set-Content -Path "$wrapperDir\gradle-wrapper.properties" -Value $wrapperProps
Write-Host "✓ Gradle wrapper configured" -ForegroundColor Green

# Create gradlew.bat
$gradlewBat = @'
@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
'@

Set-Content -Path "gradlew.bat" -Value $gradlewBat
Write-Host "✓ gradlew.bat created" -ForegroundColor Green

Write-Host ""
Write-Host "=== Running Gradle Build ===" -ForegroundColor Cyan
Write-Host "This may take several minutes on first run (downloading Gradle)..." -ForegroundColor Yellow
Write-Host ""

# Run gradlew
.\gradlew.bat assembleDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=== ✓ BUILD SUCCESSFUL ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "APK Location:" -ForegroundColor Cyan
    Write-Host "  app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor White
    Write-Host ""
    
    # Copy to Release folder
    $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        $releaseDir = "..\Release"
        if (-not (Test-Path $releaseDir)) {
            New-Item -ItemType Directory -Force -Path $releaseDir | Out-Null
        }
        Copy-Item $apkPath "$releaseDir\SystemMonitor-v3.0.apk" -Force
        Write-Host "✓ APK copied to: Release\SystemMonitor-v3.0.apk" -ForegroundColor Green
    }
} else {
    Write-Host ""
    Write-Host "=== ✗ BUILD FAILED ===" -ForegroundColor Red
    Write-Host "Check errors above" -ForegroundColor Yellow
}
