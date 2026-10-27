# Validation — canvasOS EN 1.2.1

Performed on 28 October 2026 with JDK 17, Android SDK 36 / Build Tools 36.0.0, bundled Gradle 9.8.2 and Android Gradle Plugin 9.4.1.

| Check | Result |
|---|---|
| Standard debug unit tests | 420 tests passed, 0 failures/errors/skipped |
| Guard debug unit tests | 420 tests passed, 0 failures/errors/skipped |
| Standard release build | Passed; R8/resource shrinking enabled |
| Guard release build | Passed; R8/resource shrinking enabled |
| Standard release Android lint | Passed; existing deprecation/lint warnings remain |
| English UI coverage script | Passed: 2,778 Kotlin string tokens, phone markup, locale and updater routing |
| Phone JavaScript syntax (`node --check`) | Passed |
| APK signature verification | Both APKs verified and share the English release certificate |
| APK identity | `com.aksamitsah.canvasos.en`, version 1.2.1 / code 14, min SDK 28, target SDK 31 |

Certificate SHA-256:

`e00337baca52ba867dd806253d90964b08ae388b1954ccd0de546ea15bc6dd88`

## Android TV emulator smoke test

Used the official Android TV API 31 ARM64 image (revision 4) in a task-local emulator. Its archive was verified against Google's repository SHA-1 before extraction. The user's saved emulator was not modified.

- Signed standard release installed successfully and opened without a crash.
- Inspected first-run Start/Later prompt, Home Settings, Appearance and Control Center; screenshots are in `screenshots/en/`.
- Selected Later for optional local debugging; no power/sleep action executed.
- Default-home assignment alone left Google's launcher in front, matching the documented firmware limitation.
- Granted the documented permissions, enabled Home Protection, waited for the accessibility service to bind and retested Home: English HomeActivity became foreground.
- Opening Settings and returning with Back restored English HomeActivity.
- Stock launcher was not disabled or removed.

## Limits

The English build was not installed on the owner's Amazon AFTSS Fire TV during this development task. Fire TV Home integration, physical remote behavior, manufacturer hardware controls, Bluetooth pairing, real playback/lyrics, long-running revival, sleep/reboot persistence and real-phone browser interactions require device validation. Sleep/reboot was deliberately not tested. The phone page received static language and JavaScript syntax checks, not full browser/device interaction tests. User-generated names and external app content can remain in their original language.

The upstream Astro website is preserved as reference and was not modified, deployed or tested as an English-fork website. See the build log and test/lint reports in the local release package for detailed diagnostics.
