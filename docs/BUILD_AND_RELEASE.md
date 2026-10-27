# Build and release canvasOS EN

## Build prerequisites

- JDK 17 or newer compatible with the bundled Gradle/Android Gradle Plugin.
- Android SDK `platforms;android-36` and `build-tools;36.0.0`.
- Set `ANDROID_HOME`, or create an ignored `local.properties` containing `sdk.dir=/absolute/path/to/sdk`.
- Network access to Gradle, Google Maven and Maven Central on the first build.

```sh
./gradlew --no-daemon :app:testStandardDebugUnitTest :app:testGuardDebugUnitTest
./gradlew --no-daemon :app:assembleStandardRelease :app:assembleGuardRelease
./gradlew --no-daemon :app:lintStandardRelease
python3 tools/check-english.py
```

Outputs:

- `app/build/outputs/apk/standard/release/app-standard-release.apk`
- `app/build/outputs/apk/guard/release/app-guard-release.apk`

## Identity and signing

English application ID: `com.aksamitsah.canvasos.en`. Upstream ID: `com.getisolated.canvasos`. Separate IDs let both apps remain installed. There is no automatic preference migration. Never uninstall the French app without the owner's agreement.

All distributed English releases must use the **same English signing key**. The key is distinct from upstream's key. Back up the keystore and password securely; losing them prevents in-place updates. `keystore.properties`, `*.jks`, `*.keystore` and `.env` are ignored.

Local `keystore.properties` (never commit):

```properties
storeFile=canvasos-en-release.jks
storePassword=YOUR_SECRET
keyAlias=canvasos-en
keyPassword=YOUR_SECRET
```

For GitHub Actions configure these repository secrets, without printing them:

- `CANVASOS_KEYSTORE_B64`: base64-encoded English keystore.
- `CANVASOS_KEYSTORE_PASSWORD`: keystore password.
- `CANVASOS_KEY_ALIAS`: `canvasos-en`.
- `CANVASOS_KEY_PASSWORD`: key password.

The workflow refuses tag releases without signing secrets and rejects Android Debug certificates. Pull-request builds may use debug signing and are not published as releases.

## Publication

Upstream source licensing and separately owned bundled media are documented in `SOURCE_AND_LICENSE.md`. The repository MIT license does not relicense upstream or third-party material; retain that provenance note in every release.

1. Run all checks and inspect the English UI on an emulator or TV.
2. Increment `versionCode` and `versionName` in `app/build.gradle.kts`.
3. Commit source and docs; keep signing files out of Git.
4. Configure signing secrets before creating a `v*` tag.
5. Tag the exact tested commit and push it. CI runs tests, builds both APKs, checks lint and signatures, and uploads the release assets.
6. Verify the release APK certificates and published `SHA256SUMS`.

The updater fetches `https://api.github.com/repos/aksamitsah/canvasOS-EN/releases/latest`, choosing `canvasOS.apk` or `canvasOS-guard.apk` according to the installed flavor. GitHub's latest-release API does not serve private releases to the unauthenticated TV, so private-release builds cannot update through the app until an accessible release exists. Pre-release tags are also excluded from that API.

## Device validation

Install only on an explicitly identified device. Check first-launch text, Home Settings, Control Center, profiles, weather, phone page, Home button and return from another app. Test reboot/sleep behavior only if the owner specifically agrees. Do not disable stock launchers merely to make a test pass.
