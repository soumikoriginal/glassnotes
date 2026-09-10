# Glass Notes

**Your thoughts. Your device. Your privacy.**

Glass Notes is a premium, offline-first notes application for Android featuring a beautiful Liquid Glass UI inspired by modern design principles.

## CI / Release builds

This repo builds signed release APK/AAB via GitHub Actions
(`.github/workflows/release.yml`) on every `v*` tag push. One-time setup
(keystore + repo secrets) is documented in **CI_RELEASE_SETUP.md**.

## Features

### Core Functionality
- ✓ **Text Notes** - Quick and simple note-taking
- ✓ **Checklist Notes** - Track tasks with interactive checklists
- ✓ **Rich Organization** - Pin, archive, label, and color-code your notes
- ✓ **Fast Search** - Instantly find notes with local search
- ✓ **Auto-Save** - Never lose your thoughts

### Privacy & Offline First
- ✓ **100% Offline** - No internet connection required
- ✓ **No Account** - No login, no signup, no tracking
- ✓ **Local Storage** - All data stays on your device
- ✓ **Local Backup** - Export and import your notes locally
- ✓ **Zero Analytics** - Your notes are yours alone

### Premium UI
- ✓ **Liquid Glass Design** - Beautiful translucent interface
- ✓ **Dark Mode** - System-aware theme switching
- ✓ **Smooth Animations** - Fluid transitions and interactions
- ✓ **Responsive Layout** - Works on phones, tablets, and foldables

## Technology Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM + Clean Architecture
- **Database:** Room (SQLite)
- **Minimum SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)

## Project Structure

```
app/
├── data/               # Data layer
│   ├── local/         # Room database, DAOs, entities
│   ├── repository/    # Repository implementations
│   └── backup/        # Backup/restore utilities
├── domain/            # Domain layer
│   ├── model/         # Domain models
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic use cases
├── presentation/      # Presentation layer
│   ├── home/          # Home screen
│   ├── editor/        # Note editor
│   ├── search/        # Search functionality
│   ├── labels/        # Label management
│   ├── archive/       # Archived notes
│   ├── trash/         # Deleted notes
│   └── settings/      # App settings
└── ui/                # UI components
    ├── theme/         # Theme and design system
    └── components/    # Reusable Glass UI components
```

## Building the Project

### Prerequisites
- JDK 17
- Android SDK 34+
- Gradle 8.x

### Build Commands

```bash
# Debug build
gradle assembleDebug

# Release build
gradle assembleRelease

# Install on connected device
gradle installDebug

# Run tests
gradle test
```

## Architecture

Glass Notes follows Clean Architecture principles with clear separation of concerns:

### Data Layer
- **Room Database** - Local persistence
- **Repository Pattern** - Data access abstraction
- **Type Converters** - Complex data serialization

### Domain Layer
- **Use Cases** - Single-responsibility business logic
- **Models** - Core business entities
- **Repository Interfaces** - Dependency inversion

### Presentation Layer
- **MVVM Pattern** - Separation of UI and logic
- **StateFlow** - Reactive state management
- **Jetpack Compose** - Modern declarative UI

## Key Components

### Glass UI System
Custom components implementing the Liquid Glass aesthetic:
- `GlassCard` - Translucent cards with blur
- `GlassButton` - Glass-style buttons
- `GlassTextField` - Transparent input fields
- `GlassTopBar` / `GlassBottomBar` - Navigation bars
- `GlassBottomSheet` - Modal bottom sheets
- `GlassFAB` - Floating action button

### Note Features
- **Multiple Types** - Text, checklist, image, drawing
- **Organization** - Pin, archive, labels, colors
- **Metadata** - Creation and modification timestamps
- **Search** - Full-text search across all notes
- **Reminders** - Local notification support (planned)

## Privacy by Design

Glass Notes is built with privacy as a core principle:

1. **No Network Access** - The app doesn't request internet permission
2. **No Cloud Sync** - All data remains on your device
3. **No Analytics** - Zero tracking or data collection
4. **No Third-Party SDKs** - No external dependencies that phone home
5. **Local Backup Only** - You control where your data goes

## Roadmap

### Completed ✓
- [x] Core note-taking functionality
- [x] Checklist support
- [x] Search functionality
- [x] Labels and organization
- [x] Archive and trash
- [x] Color customization
- [x] Dark mode
- [x] Local backup/restore
- [x] Liquid Glass UI

### Planned
- [ ] Image attachments
- [ ] Drawing canvas
- [ ] Reminder notifications
- [ ] App lock with biometric auth
- [ ] Swipe gestures
- [ ] Note sharing
- [ ] Export to PDF/Markdown
- [ ] Widget support

## Contributing

This is a reference implementation of the Glass Notes concept. Feel free to fork and adapt for your needs.

## License

[To be determined - specify your license here]

## Credits

Developed as a premium, privacy-focused alternative to cloud-based note-taking apps.

Design inspired by modern Liquid Glass aesthetics while maintaining Android native feel.

---

**Glass Notes** - Write. Organize. Remember. Offline.
