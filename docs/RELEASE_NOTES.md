# canvasOS EN 1.2.1

English edition based on upstream canvasOS 1.2.0, commit `f3e4dff07c65b140bf0bd4fc288ebb37093d0370`.

- English TV setup, Home settings, Control Center, profiles, folders, shortcuts, weather, playback and errors.
- English phone remote, uploads and configuration page.
- English dates, sorting, weather lookup, TMDB metadata and wallpaper captions.
- Separate app ID `com.aksamitsah.canvasos.en`: coexistence with the original French app; no uninstall required and no automatic settings migration.
- Stable English release signing key and updates from `aksamitsah/canvasOS-EN`.
- English installation, rollback, build, privacy and project documentation.

`canvasOS.apk` is the standard launcher; `canvasOS-guard.apk` is the app-only variant for firmware that refuses HOME apps, including some TCL models. Both require Android 9+.

This is a launcher, not replacement firmware. Do not disable or uninstall the original TV launcher. Fire TV Home-button integration requires separate device testing and is not guaranteed by successful installation.

Checks: standard and guard unit tests, signed release builds, Android lint, English-copy checks and phone JavaScript syntax. See `docs/VALIDATION.md` for recorded results and visual checks.

Verify `SHA256SUMS` after downloading. Future English builds signed with this same key can update the English package in place. They do not overwrite upstream canvasOS.

Upstream source attribution and notices are retained. Upstream's missing top-level source-code license and separately owned media are documented in `SOURCE_AND_LICENSE.md`; this release must not be represented as an official upstream release or a grant of rights to upstream material.
