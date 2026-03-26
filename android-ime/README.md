# Afrikaans/English Keyboard — Native Android IME

A fully native Android Input Method Editor (IME) built in Kotlin. It appears in
the system keyboard selector alongside Gboard, SwiftKey, etc., and works inside
every app on the device.

---

## Features

| Feature | Detail |
|---|---|
| **Languages** | English (en_US) and Afrikaans (af_ZA) — toggle with the LANG key |
| **Spell-check** | Word suggestion strip appears above the keyboard for unknown words |
| **Numeric row** | 1–0 row above the main rows (toggle in Settings) |
| **Symbols** | Page 1: punctuation/operators · Page 2: accented/Afrikaans characters |
| **Caps lock** | Single-tap = one-shot shift · Double-tap = caps-lock |
| **Resizable** | Drag a SeekBar in Settings to set height (200–380 dp) |
| **Haptic feedback** | Short vibration on every key press (toggle in Settings) |
| **Launcher activity** | Guides the user to enable & set as default keyboard |

---

## Project structure

```
android-ime/
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/afrikaanskeyboard/
│       │   ├── AfrikaansEnglishIME.kt   ← InputMethodService (core)
│       │   ├── CustomKeyboardView.kt    ← programmatic keyboard UI
│       │   ├── CandidatesView.kt        ← horizontal suggestion strip
│       │   ├── DictionaryManager.kt     ← word sets + spell-check logic
│       │   ├── KeyboardData.kt          ← key layout definitions
│       │   └── SettingsActivity.kt      ← launcher + PreferenceFragment
│       └── res/
│           ├── drawable/                ← key background shapes + launcher icon
│           ├── layout/                  ← activity_settings.xml
│           ├── mipmap-*/                ← adaptive launcher icons
│           ├── values/                  ← colors, strings, styles
│           └── xml/
│               ├── method.xml           ← IME registration + subtypes
│               └── keyboard_preferences.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

---

## How to build

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer — [download](https://developer.android.com/studio)
- **JDK 17** (bundled with Android Studio)
- An Android device or emulator running **API 24+** (Android 7.0+)

### Steps

1. **Open the project**
   ```
   File → Open → select the `android-ime/` folder
   ```

2. **Let Gradle sync** (happens automatically; downloads dependencies)

3. **Build & install a debug APK**
   ```
   Build → Build APK(s)          # produces app/build/outputs/apk/debug/app-debug.apk
   Run → Run 'app'               # installs directly on a connected device / emulator
   ```

4. **Enable the keyboard on the device**

   The app installs a launcher activity that walks you through two taps:
   - **Enable keyboard** → opens Android Settings → System → Languages & Input → Virtual keyboard
   - **Set as default** → opens the input method picker

   After both steps the keyboard is active everywhere.

### Build a release APK (for sideloading)

```bash
# From the android-ime/ directory:
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk

# Sign with your keystore (create one with Android Studio: Build → Generate Signed Bundle/APK):
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore your.keystore app-release-unsigned.apk your_alias

zipalign -v 4 app-release-unsigned.apk app-release.apk
```

---

## Extending the dictionaries

Open `DictionaryManager.kt`. The `ENGLISH` and `AFRIKAANS` sets are plain Kotlin
string sets. Add or remove words there. For production use you could load words
from a text file in `assets/` using `context.assets.open("en_words.txt")`.

---

## Customising the keyboard layout

Open `KeyboardData.kt`. Each row is a `List<KeyDef>`. Change `widthWeight` to
adjust relative key widths, or add/remove keys from any row.

---

## Architecture notes

```
User taps key
    ↓
CustomKeyboardView.handleKeyTap()
    ↓
KeyListener callback (AfrikaansEnglishIME)
    ↓
InputConnection.commitText() / deleteSurroundingText()
    ↓
DictionaryManager.getSuggestions()
    ↓
CandidatesView.setSuggestions()   →  user taps suggestion  →  applySuggestion()
```

`AfrikaansEnglishIME` extends `InputMethodService` and is bound to Android's
input subsystem via the `<service android:permission="android.permission.BIND_INPUT_METHOD">` 
declaration in `AndroidManifest.xml`. No special permissions are needed by the app itself.
