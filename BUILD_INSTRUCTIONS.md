# Glass Notes - Build Instructions

## Prerequisites (Already Available on Device)
- JDK 17
- Android SDK 36
- Gradle 8.14.3
- ARM64 Build Tools 35.0.0

## Quick Build Commands

### Option 1: Debug APK (Recommended)
```bash
gradle assembleDebug
```

### Option 2: Clean + Build
```bash
gradle clean assembleDebug
```

### Option 3: Install on Connected Device
```bash
gradle installDebug
```

## APK Output Location
```
app/build/outputs/apk/debug/app-debug.apk
```

## Project Statistics

### File Counts:
- Kotlin files: 56
- XML resources: 5
- Total project files: 62
