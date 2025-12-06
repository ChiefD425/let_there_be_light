# Let There Be Light

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Version](https://img.shields.io/badge/version-0.1.0-green.svg)

An open-source Android application for controlling addressable LEDs. Seamlessly manage multiple controllers, create custom patterns, and sync your lights.

## Features
- **Multi-Device Sync**: Control multiple ESP8266/ESP32 controllers.
- **Custom Patterns**: Create and save unique light patterns.
- **Scheduling**: Automate your lights.
- **Modern UI**: Built with Jetpack Compose and specialized for a premium feel.


## Getting Started

### Prerequisites
- Android Studio Ladybug or newer.
- JDK 17 (embedded in Android Studio).

### Building the APK
To build the installation file (APK) manually:

1. Open a terminal in the project root.
2. Run the build command:
   ```bash
   ./gradlew assembleDebug
   ```
   *(Windows users run `gradlew.bat assembleDebug`)*
3. Locate the APK file at:
   `app/build/outputs/apk/debug/app-debug.apk`

### Installing on Your Phone
Since this app is not on the Play Store, you must "sideload" it:

1. **Transfer the APK**: Email the file to yourself, save it to Google Drive, or use a USB cable to copy `app-debug.apk` to your phone's 'Downloads' folder.
2. **Open the File**: On your phone, locate the file (using Files app, Gmail, or Drive) and tap it.
3. **Allow Installs**: Your phone will likely warn you about installing from unknown sources.
   - Tap **Settings** on the prompt.
   - Toggle **Allow from this source**.
   - Go back and tap **Install**.
4. **Run**: Once installed, open "Let There Be Light" from your app drawer.

### Development
1. Clone the repository:
   ```bash
   git clone https://github.com/ChiefD425/let_there_be_light.git
   ```
2. Open in Android Studio.
3. Sync Gradle and Run to deploy directly to a connected device via USB.

## Documentation
- [Architecture](architecture.md): System diagrams and layer explanations.
- [Agent Context](agents.md): Developer notes and AI agent instructions.
- [Changelog](CHANGELOG.md): History of changes.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
