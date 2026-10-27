# Privacy and network behavior

This English edition retains upstream functionality and defaults. Translation does not add analytics or a cloud account.

| Feature | Requests / local access | Control |
|---|---|---|
| Weather (on by default) | Open-Meteo forecasts and city search. ip-api.com once to estimate location from public IP until a city is chosen. | Home Settings → Appearance → Weather; choose a city from the phone page. |
| Lyrics (on by default) | Sends track title, artist, album and duration to LRCLIB for each new track. | Home Settings → Playback and Notifications → Lyrics. |
| Now Playing / Home Player | Reads local media-session title, artist, artwork and playback state through notification-listener access. | Playback and Notifications. |
| Notification banners (on by default) | Reads titles and text from non-excluded app notifications; shows for 6 seconds, not recorded. Overlay permission permits banners above other apps. | Disable banners or exclude apps. |
| Top Shelf | Local Android TV Watch Next rows; TMDB/JustWatch metadata with your key; supported web feeds. | Top Shelf settings, Watch Next toggle and mode. |
| Wallpapers | Downloads from selected photo source or your custom URLs; credits vary by provider. | Wallpaper and Photo Frame. |
| Aerial screensaver | Streams videos from Apple, roughly 15–20 Mbit/s. | Screensaver content. |
| Updates | Checks this fork's GitHub Releases daily and on request. Downloads/installations require the second explicit press. | System → Update. |
| Phone page | HTTP server on the TV's LAN, port 8790, protected by the secret URL code. Photos, keys, profiles and preferences stay on the TV. | Phone Page toggle and Regenerate Code. |
| Home Protection | Accessibility observes foreground-window changes to bring Home back, not screen contents. Optional TV-key capture sees key events across apps but handles assigned TV keys on Home. | System → Home Protection; Remote Control → Capture TV Buttons. |
| Local debugging | Enables privileged system actions, APK installation and full remote control after TV authorization. | System → Local Debugging; TV developer options. |

Do not share phone-page secret URLs. Regenerate the code if a phone is lost. The page uses HTTP on the local network; it is not an Internet-hosted account service. Keep it on a trusted network.

Keys and profiles remain on the device; do not paste API keys, PINs or secret URLs into GitHub issues. More complete third-party source notices are in `THIRD_PARTY_NOTICES.md`.
