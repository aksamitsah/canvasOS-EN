# canvasOS EN project details

## Provenance

Upstream: https://github.com/getisolated/canvasOS
Upstream commit: `f3e4dff07c65b140bf0bd4fc288ebb37093d0370`
Upstream app version: 1.2.0 (code 13).
English app version: 1.2.1 (code 14).
English repository: https://github.com/aksamitsah/canvasOS-EN

canvasOS replaces the Android TV home screen. It does not replace Android, flash firmware, root the TV or remove installed streaming apps.

## Architecture

| Area | Implementation |
|---|---|
| Home, Dock, app grid | `HomeActivity.kt`, `TileAdapter.kt`, `AppRepo.kt` |
| Glass rendering and controls | `Glass.kt`, `Ui.kt`, `ControlCenter.kt`, `CcLayout.kt` |
| Settings/preferences | `SettingsActivity.kt`, `Prefs.kt` |
| Profiles/folders | `Profiles.kt`, `Folders.kt`, `FolderOverlay.kt` |
| Media/lyrics/notifications | `NowPlaying.kt`, `MediaSessions.kt`, `HomePlayer.kt`, `Lyrics.kt`, `LyricsView.kt`, `Banners.kt` |
| Wallpaper/screensaver | `Wallpapers.kt`, `WallpaperActivity.kt`, `PhotoFrame.kt`, `Aerials.kt`, `Intro.kt`, `Brand.kt` |
| TV hardware | `tv/AndroidAdapter.kt`, Sony, Xiaomi, TCL and Coocaa adapters selected by `TvControls.kt` |
| Home protection/revival | `Guard.kt`, `KeeperService.kt`, accessibility and notification-listener services |
| Local privileged commands | `Cleaner.kt` (LocalAdb), `Processes.kt`, `Installer.kt`, `SleepTimer.kt` |
| Phone HTTP/API/mDNS | `PhoneServer.kt`, `PhoneApi.kt`, `Mdns.kt`, `assets/web/index.html` |
| App updates | `Updater.kt`, GitHub Releases, standard/guard asset selection |
| Website | Upstream Astro site in `website/`; retained as upstream reference, not deployed or advertised as the English fork website |

Source directory names retain the upstream layout for easy comparison; Kotlin namespaces and manifest components use `com.aksamitsah.canvasos.en`.

## Build stack

Android Gradle Plugin 9.4.1, bundled Gradle 9.8.2, JDK 17, compile SDK 36, target SDK 31, minimum SDK 28. Release builds use R8 and resource shrinking. The standard flavor declares HOME; guard does not. Kotlin compilation is provided by the Android plugin.

Dependencies: AndroidX RecyclerView, Palette and Media3 ExoPlayer; dadb for local ADB; NanoHTTPD for phone HTTP; ZXing for QR codes; JUnit and org.json for local tests. Inter font and Material icon paths are bundled; aerial video URLs are catalogued but aerial videos stream at runtime.

## English changes

User-facing Kotlin literals and XML labels are translated, including accessibility descriptions and privileged-operation errors. The phone HTML/JavaScript is translated without renaming API paths, DOM IDs or preference keys. Display sorting/dates use English locales; weather geocoding and TMDB metadata requests ask for English. Bing and Wikimedia captions request English. TMDB provider availability still uses France, preserving upstream market behavior.

Application ID, components, custom broadcast actions, test namespaces and updater repository are changed consistently. Original French docs remain in `README.fr.md` and `AGENTS.fr.md`. Stable signing files are ignored and are not source/release assets.

## Limits

This is an English-only edition, without an in-app language switch. User content and names supplied by third-party apps or feeds are not automatically translated. Upstream website reference screenshots remain French. TV manufacturer controls and Home interception depend on firmware. The installed Amazon AFTSS accepts upstream APK installation, but this English edition has not been installed on the owner's TV as part of development. See `VALIDATION.md` for actual tests.

See `PRIVACY.md` for network defaults and `SOURCE_AND_LICENSE.md` for the upstream license gap. An automatically generated MIT license in a new repository does not establish a license for upstream code or third-party media.
