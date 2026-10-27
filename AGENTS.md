# Installing canvasOS EN — instructions for AI agents

Install the English canvasOS launcher from the user's computer onto their identified Android TV, then verify Home behavior together. Follow these steps in order. Speak the user's language. This is a household device: ask whenever a step below requires a choice or approval.

This edition uses `com.aksamitsah.canvasos.en`, **not** upstream's `com.getisolated.canvasos`. Its TV interface and phone page are English. Do not follow the upstream French guide's package IDs when operating this edition. `AGENTS.fr.md` preserves the original upstream reference; it is not this fork's installation command source.

## Guiding the owner

Explain the sequence: find TV, enable debugging if needed, install, verify; allow about ten minutes. Explain that it is reversible. Do computer work yourself; the owner uses only the remote.

Give **one TV action at a time**: menu, button, expected screen, choice. Wait for confirmation before the next action. Discover IP, model and Android version yourself where possible. Translate command output into ordinary language. Once ADB works, inspect screenshots before giving directions. Warn before any action that changes the displayed screen or opens a permission prompt. Do not press OK blindly.

At completion report what was installed/enabled, default weather/lyrics/notification behavior, how to update and exact rollback commands using saved original values.

## Absolute rules

- Never power off, reboot or sleep the TV without explicit approval. Do not run `svc power shutdown`, `svc power reboot`, `reboot`, `KEYCODE_POWER` or `KEYCODE_SLEEP` during installation.
- Never uninstall system apps or any TV launcher. Ask before uninstalling or disabling anything, including a previous canvasOS build.
- **Never disable Xiaomi/Redmi's `com.mitv.tvhome`.** Its fallback may also reject third-party launchers, resulting in a black screen.
- Connect with ADB only to the TV the user confirmed: connection can show an authorization dialog on the target.
- Before sending DPAD_CENTER or ENTER, inspect focus by screenshot and/or UI dump. Power and Sleep buttons exist in Control Center.
- Do not change unrelated TV settings or optional canvasOS settings without the user's request.
- Do not replace an installed app with a differently signed build by uninstalling it without approval.

## 0. Prerequisites

Check `adb version`; install Android SDK Platform-Tools if missing, explaining why. `curl` is sufficient for APK downloads; GitHub CLI and a source clone are optional. The computer and TV must share a trusted LAN. Android 9/API 28 is the minimum.

## 1. Find the TV and connect

Keep the TV on. Cross-check these sources by IP without first issuing `adb connect` to every address:

1. `adb devices -l`: an IP at `device` is already authorized.
2. mDNS `_androidtvremote2._tcp` and `_googlecast._tcp`. Cast TXT has `fn` (friendly name) and `md` (model). Audio devices advertise Cast too; require Android TV remote service or open port 5555.
3. Scan port 5555 on the computer's own subnet using short TCP probes. Do not scan unrelated networks.

On macOS, `dns-sd -B` discovers service instances; `dns-sd -L` resolves the chosen instance, and `dns-sd -G v4` resolves its host. Stop each discovery process after a few seconds; use a terminal if output is buffered. On Linux use `avahi-browse -rpt`. Discover subnet/interface from the local routing table, not by asking the user for an address first.

Present names/models and ask which TV is theirs. If only an unnamed ADB candidate exists, explain that the authorization prompt will identify it and get agreement before connecting. If none is found, confirm the TV is on, connected and on the same non-guest network; retry. Ask the owner to read the IP in network settings only as a last resort.

### Enable debugging, one remote action at a time

- Google TV / recent Sony: Settings → System → About → Android TV OS Build; press OK seven times. Then System → Developer Options → USB Debugging (sometimes Network/ADB Debugging).
- Older Android TV: Settings → Device Preferences → About → Build; seven OK presses. Then Device Preferences → Developer Options → USB Debugging.
- Fire TV: Settings → My Fire TV → About; on firmware that hides developer options, select the device name seven times. Then My Fire TV → Developer Options → ADB Debugging. Labels depend on firmware: ask what appears if needed.

Do not give the entire sequence for the owner to perform unassisted. Verify port 5555 after enabling debugging.

Before connection, explain the “Allow USB debugging?” dialog. The owner should select “Always allow from this computer”, then “Allow”.

```sh
adb connect TV_IP:5555
adb -s TV_IP:5555 get-state
```

If unauthorized, check the state periodically while waiting for the owner; do not repeatedly ask whether they finished. If the prompt vanished, disconnect/reconnect the same confirmed TV. If refused, revisit debugging.

## 2. Inspect and record original settings

```sh
adb -s TV_IP:5555 shell getprop ro.build.version.sdk
adb -s TV_IP:5555 shell getprop ro.product.manufacturer
adb -s TV_IP:5555 shell getprop ro.product.model
adb -s TV_IP:5555 shell cmd package resolve-activity -a android.intent.action.MAIN -c android.intent.category.HOME
adb -s TV_IP:5555 shell settings get secure screensaver_components
adb -s TV_IP:5555 shell settings get secure enabled_accessibility_services
adb -s TV_IP:5555 exec-out screencap -p > tv-before.png
```

Save the original home component, screensaver and accessibility list for rollback. Stop if API < 28. Check for both English and upstream canvasOS packages. Explain that this edition coexists with the French app and does not migrate its preferences. Never disable the other edition's services without agreement.

## 3. Install the English edition

Explain that installation is starting and no remote action is required. Download from **this fork**, not upstream:

```sh
curl -fLo canvasOS.apk https://github.com/aksamitsah/canvasOS-EN/releases/latest/download/canvasOS.apk
adb -s TV_IP:5555 install -r canvasOS.apk
```

Verify published checksums and signing fingerprint when available. If the repository is private, unauthenticated URLs and the in-app updater cannot retrieve releases; use the signed local build or an authenticated download. Some TCL package verifiers require `canvasOS-guard.apk`, which does not declare HOME.

Every English release must carry this fork's stable key. `INSTALL_FAILED_UPDATE_INCOMPATIBLE` means a different key signed the installed English package. Explain settings loss and get approval before removing that package; first turn off its Home Protection. Never uninstall upstream or system launchers as an automatic workaround.

For local builds see `docs/BUILD_AND_RELEASE.md`. Debug-signed builds cannot update stable-key releases. Later updates can be installed in Home Settings → System → Update after local debugging authorization: press once to check, again to download/install. The daily check does not automatically download.

## 4. Make it Home

Warn that the display will change. Explain notification access and overlays before enabling them: banners read notification titles/text locally for display, and Now Playing reads media metadata; both are on by default. Explain secure settings access is used to maintain Home Protection.

```sh
adb -s TV_IP:5555 shell cmd package set-home-activity com.aksamitsah.canvasos.en/com.aksamitsah.canvasos.en.HomeActivity
adb -s TV_IP:5555 shell input keyevent KEYCODE_HOME
adb -s TV_IP:5555 shell pm grant com.aksamitsah.canvasos.en android.permission.WRITE_SECURE_SETTINGS
adb -s TV_IP:5555 shell cmd notification allow_listener com.aksamitsah.canvasos.en/com.aksamitsah.canvasos.en.RevivalListener
adb -s TV_IP:5555 shell appops set com.aksamitsah.canvasos.en SYSTEM_ALERT_WINDOW allow
adb -s TV_IP:5555 shell pm grant com.aksamitsah.canvasos.en android.permission.READ_TV_LISTINGS
```

On Android 12+ also grant `android.permission.BLUETOOTH_CONNECT`. Older Android versions do not have it. READ_TV_LISTINGS may be refused on devices without TvProvider; explain this only limits Continue Watching.

If the stock launcher opens despite assignment, open the English activity directly and enable Home Protection:

```sh
adb -s TV_IP:5555 shell am start -n com.aksamitsah.canvasos.en/com.aksamitsah.canvasos.en.HomeActivity
adb -s TV_IP:5555 shell am broadcast -a com.aksamitsah.canvasos.en.CONFIG -n com.aksamitsah.canvasos.en/com.aksamitsah.canvasos.en.ConfigReceiver --ez guard true
```

Home Protection observes window changes and returns canvasOS when the stock launcher appears; it does not read screen contents. It can be turned off in Home Settings → System → Home Protection. Never treat command success as proof the physical Home button works. Fire TV may ignore assignment or restrict the service.

## 5. Verify

Take and view a screenshot, then ask the owner if they see the same screen. Expected: photo background (a gradient during initial downloads), compact clock, controls/profile at the top right, app tiles in a glass Dock near the bottom. A media capsule may appear at the top left.

Warn before testing Home. Check the foreground activity after pressing Home, and after opening a harmless app and leaving with Back. It must be `com.aksamitsah.canvasos.en.HomeActivity`. Press Back on Home only to inspect Control Center; do not select Power or Sleep. Record any unsupported brightness/input/Bluetooth controls.

If Home still fails, retry assignment once and Home Protection. Do not silently disable a launcher. On non-Xiaomi devices only, if all protection methods fail, explain any proposed stock-launcher disable operation and its exact `pm enable` rollback; ask first. Never uninstall it. If the firmware will not accept a replacement, say so: canvasOS remains usable as an app.

## 6. Local debugging authorization

First launch shows “Enable Power and Memory Controls”, with “Start” and “Later”. This grants capability; it must not actually power off the TV. Tell the owner that Start will open “Allow debugging?”. Inspect the screen, guide selection one action at a time, and have them select “Always allow” and “Allow”. If they choose Later, revisit Home Settings → System → Local Debugging.

For any automated OK press, inspect `focused="true"` text/bounds:

```sh
adb -s TV_IP:5555 shell uiautomator dump /sdcard/u.xml
adb -s TV_IP:5555 shell cat /sdcard/u.xml
```

Local debugging supports APK installation, updates, full phone remote, memory cleanup and system controls. Do not test its power capability by turning off or sleeping the TV.

## 7. Sony BRAVIA only

For brightness, volume and inputs, register client `tvhome:1` via the TV's `/sony/accessControl` API using `actRegister`, nickname `TVHome`, level `private`. Warn that a four-digit PIN will appear. Read it by screenshot or ask the owner, then repeat registration with HTTP authentication `:<PIN>`. If remote control is disabled, guide the owner in the TV's remote-device/network settings before retrying.

Check Control Center's brightness and current-input indicators without changing volume/inputs unexpectedly. Skip this section on non-Sony devices.

## 8. Optional settings and data sources

Mention the following; change only at the owner's request. Settings groups: Appearance, Wallpaper and Photo Frame, Top Shelf, Playback and Notifications, Memory and Power, Apps, Profiles, Phone Page, Remote Control, System.

- Wallpaper: Daily Photos (Bing), Unsplash with the user's key, Scenery, Cute Animals, Anime, Masterpieces, My Photos, Custom Link, or gradients. Choose change interval or Never. Phone page uploads photos/links; source selection is on the TV.
- Phone Page: same-LAN QR code, HTTP port 8790, trackpad/keyboard, photos, icon/video uploads, profile photos, app artwork and APK installs. URL secret is its protection. Regenerate Code invalidates old links; switch off to stop serving. `canvasos.local` works on recent phones. Do not publish its secret URL.
- Photo Frame: screensaver, idle delay, per-photo duration, photos or streaming aerial videos. Aerials stream from Apple at roughly 15–20 Mbit/s. Record original screensaver before changing it.
- Folders: hold an app to create/add to a folder; hold folders to rename, add apps, move or delete. Folder deletion returns apps to Home. No CONFIG folder API.
- Profiles: independent Home layout, hidden apps, names, folders, inputs and wallpaper; other settings shared. Child PIN is 4–6 digits, required to leave/open settings/uninstall. It does not protect the TV's own settings. Main profile cannot be deleted. Avatar uploads are available on the phone page. Never publish PINs.
- App artwork/names: phone Apps tab; 16:9 image, 20 MB maximum. Long-press menus offer Rename and Original Image. Names are profile-specific, artwork shared.
- HDMI: Control Center → Input → Show on Home. Availability depends on manufacturer APIs.
- Bluetooth: lists paired devices; headphones/speakers can connect or disconnect where firmware permits. Pairing remains in TV settings. Nearby Devices permission on Android 12+.
- Memory cleanup preserves active playback, foreground services, accessibility services, keyboard and configured Apps to Keep. Last Cleanup shows results. Do not run cleanup merely to test navigation.
- Sleep Timer: explicit user-selected delay only; does not survive reboot. Never set it during setup without request.
- Top Shelf: local Watch Next/Continue Watching, then TMDB/JustWatch with user key and supported web feeds. English metadata; provider availability region remains France. Modes Full, Android TV Only, Off. Keys stay on TV.
- Weather on by default: Open-Meteo forecasts; initial ip-api.com public-IP location estimate. Explain this. User may choose a city or switch weather off.
- Now Playing/Home Player on by default: local media metadata through notification access, not audio recording. Home capsule opens the playing app; Control Center holds playback controls.
- Lyrics on by default: track title, artist, album and duration sent to LRCLIB on each new track. Explain this. Disable in Playback and Notifications or phone Settings to stop requests.
- Notification banners on by default: non-excluded app notification titles/text read locally, shown for six seconds, not stored; overlays allow display above other apps. Disable or exclude apps as requested.
- Appearance: clock, profile photo, Recent/Fixed Dock, selection effects, Glass/Panel Control Center, logo, startup animation and delay. Do not fetch additional protected brand clips.
- Control Center editing: hold OK on its Home button, or Appearance → Customize Control Center. 4×6 grid, up to three pages. Move, resize, remove/add controls or pages, reset. Back finishes. No CONFIG layout API.
- Remote shortcuts: choose function then press/hold a button. Home/volume/power/arrows/OK cannot be rebound. Capture TV Buttons uses the accessibility service and sees key events in all apps, handling only assigned TV keys on Home; explain this before enabling.

CONFIG uses the English action/component shown in step 4. Supported keys remain unchanged from upstream: `guard`, `keep_adb`, `clock_24`, `clock_style`, `focus_style`, `cc_style`, `intro`, `intro_source`, `intro_sound`, `wake_delay`, `brand`, `settings_icon`, `background`, `phone_server`, `dock_recents`, `weather`, `weather_city`, `now_playing`, `home_player`, `lyrics`, `lyrics_align`, `banners`, `banner_excluded`, `player_style`, `unsplash_key`, `shelf`, `watch_next`, `tmdb_key`, `profile_add`, `profile_restricted`, `profile`, `profile_pin`, `check_update`. Key values and protocol names are not translated. Never log API keys or PINs. A query without extras reports current configuration. Consult `Guard.kt`/`Prefs.kt` for exact accepted values.

## 9. Adapting unsupported TVs

Do this only on request or after approval to investigate a specific missing function. Add a brand adapter under `tv/`, extending AndroidAdapter and registering in TvControls; do not modify unrelated brand behavior. Snapshot original values before any write and restore them after experiments.

Volume: inspect `cmd media_session volume --stream 3 --get`, audio/HDMI-CEC state. Inputs: inspect `dumpsys tv_input`, observe a remote source switch and the relevant intent/logcat. Brightness: compare system/global/secure settings before and after an owner-selected picture-setting adjustment; inspect manufacturer-local APIs. Any test that changes the screen, input or sound needs advance explanation. Build and verify the changed adapter; distinguish working and unsupported functions. An adapted build must retain a stable English signature for in-place updates.

## 10. Home Protection and revival

The accessibility Home guard returns canvasOS after stock Home appears. The notification listener revives the app and restores Home Protection after the system kills it. Secure settings permission lets it restore its service and screensaver. Confirm the service is in `enabled_accessibility_services`, actually bound in `dumpsys accessibility`, and the expected activity is foreground after physical Home.

Do not force a sleep or reboot verification. If the owner expressly agrees later, explain expected behavior first. Protection waits about 1.5 seconds after startup/wake to avoid interfering with HDMI-CEC input selection. If it cannot work on that firmware, report the limitation.

## 11. Preinstalled apps

Only on explicit request. Identify every proposed app by label/package/path and explain its role. Never remove settings, launchers, keyboards, TV/tuner/input players, System UI, updates, Google Play services or unidentified packages. This installation task does not authorize debloating.

## Remove or restore

Use saved original values, never guesses. Warn that the display may return to stock Home. With the owner's approval, first stop English Home Protection, restore the screensaver only if changed, then uninstall only the English package:

```sh
adb -s TV_IP:5555 shell am broadcast -a com.aksamitsah.canvasos.en.CONFIG -n com.aksamitsah.canvasos.en/com.aksamitsah.canvasos.en.ConfigReceiver --ez guard false
adb -s TV_IP:5555 shell settings put secure screensaver_components ORIGINAL_SCREENSAVER
adb -s TV_IP:5555 uninstall com.aksamitsah.canvasos.en
```

Restore original Home assignment if needed, using the recorded component. Verify the original accessibility list is preserved. If a stock launcher was disabled with approval, re-enable its exact package first. Never run placeholder commands literally. Provide the owner a rollback note with actual device address and original values. The French package is separate and remains untouched unless removal is separately requested.
