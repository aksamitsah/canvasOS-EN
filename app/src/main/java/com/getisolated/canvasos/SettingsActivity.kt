package com.aksamitsah.canvasos.en

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.aksamitsah.canvasos.en.tv.TvControls

/**
 * tvOS-style settings, laid out on Apple TV's settings page (1080p measures / 2, see [u]): the
 * title centred on top, the icon card on the left with a description of the focused row low in
 * that column, and rows on the right under small uppercase headers, all over the home wallpaper
 * blurred and darkened. The top level lists the groups ([Page.title]); each opens a page of rows,
 * and the list pages (keep list, hidden apps…) sit one level below their group. All reuse the same list.
 */
class SettingsActivity : Activity() {
    companion object {
        const val SONY_DREAM = "com.sony.dtv.braviashow/com.sony.dtv.braviashow.BraviaDayDreamService"
        const val ICON_FILE = "settings_icon.png"
        /** Opens the settings on a sub-page rather than the main one: [PAGE_PROFILES] or [PAGE_LOOK]. */
        const val EXTRA_PAGE = "com.aksamitsah.canvasos.en.PAGE"
        const val PAGE_PROFILES = "profiles"
        /** Apparence, where the weather is switched on (the Control Center's idle Météo control leads there). */
        const val PAGE_LOOK = "look"

        /** Where the settings icon is read from, with a key that changes whenever the picture does. */
        class IconSource(val key: String, val open: () -> java.io.InputStream)

        /**
         * The settings icon in use ([Prefs.settingsIcon]): the chosen brand's title card from the APK
         * ([Brand]), a picture of the user's own uploaded from the phone page or pushed over adb to
         * Android/data/com.aksamitsah.canvasos.en/files (the title card again while there is none), or
         * null for the built-in gear.
         */
        fun iconSource(ctx: android.content.Context): IconSource? {
            val prefs = Prefs(ctx)
            val own = if (prefs.settingsIcon == Prefs.ICON_OWN) iconFile(ctx) else null
            return when {
                prefs.settingsIcon == Prefs.ICON_GEAR -> null
                own != null -> IconSource("own@" + own.lastModified()) { own.inputStream() }
                else -> IconSource(prefs.brand) { Brand.openIcon(ctx, prefs.brand) }
            }
        }

        /** The uploaded picture whether or not it is in use ([Prefs.settingsIcon]). */
        fun iconFile(ctx: android.content.Context): java.io.File? =
            listOfNotNull(java.io.File(ctx.filesDir, ICON_FILE), ctx.getExternalFilesDir(null)?.let { java.io.File(it, ICON_FILE) })
                .firstOrNull { it.isFile && it.length() > 0 }

        /** The icon decoded at no more than about [width] px wide: a 1280 px picture shown at 600 needn't take 6 MB. */
        fun decodeIcon(src: IconSource, width: Int): android.graphics.Bitmap? = decode(src.open, width)

        /** The same for a picture on disk (an app's own tile, [AppRepo.customTileFile]); null if it cannot be read. */
        fun decodeIcon(f: java.io.File, width: Int): android.graphics.Bitmap? = decode({ f.inputStream() }, width)

        private fun decode(open: () -> java.io.InputStream, width: Int): android.graphics.Bitmap? {
            val o = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            runCatching { open().use { android.graphics.BitmapFactory.decodeStream(it, null, o) } }.getOrElse { return null }
            var sample = 1
            while (o.outWidth / (sample * 2) >= width) sample *= 2
            return runCatching {
                open().use { android.graphics.BitmapFactory.decodeStream(it, null, android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }) }
            }.getOrNull()
        }

        /** A profile's avatar at the start of its row (drawn at this size, shown at [ROW_AVATAR]), and as the big icon of the profiles page. */
        private const val ROW_AVATAR_DP = 36
        private const val ROW_AVATAR = 22f
        private const val BIG_AVATAR = 110f

        // Measured on Apple TV's settings page (white at these alphas over its dark teal background).
        private val TITLE = Color.argb(85, 255, 255, 255)
        private val IDLE_TEXT = Color.WHITE
        private val IDLE_VALUE = Color.argb(95, 255, 255, 255)
        private val FOCUS_VALUE = Color.argb(125, 0, 0, 0)
        /** A row at rest: a light translucent fill over the blurred wallpaper. */
        private val IDLE_ROW = Color.argb(25, 255, 255, 255)
        private val HEADER = Color.argb(95, 255, 255, 255)
        private val DESCRIPTION = Color.argb(158, 255, 255, 255)
        /** Behind everything until the blurred wallpaper is ready, and when there is none. */
        private val BASE = Color.rgb(22, 27, 36)
        /** The wallpaper's brightest allowed luminance once blurred: Apple TV's page never goes past about this. */
        private const val MAX_LUMA = 62f

        // The layout, in units of the 960-wide design (a 1080p screenshot's pixels / 2).
        private const val TITLE_TOP = 29f
        private const val LIST_WIDTH = 390f
        private const val LIST_END = 40f
        /** Where the scrolling list starts: rows slide up under a short fade below the title. */
        private const val LIST_TOP = 70f
        private const val CARD_W = 200f
        private const val CARD_H = 120f
        private const val CARD_TOP = 193f
        private const val ABOUT_TOP = 416f
        private const val ABOUT_W = 440f
        private const val ROW_H = 33f
        private const val ROW_GAP = 7f
        private const val ROW_RADIUS = 13f
        private const val ROW_PAD = 16f
        private const val HEADER_ABOVE = 16f
        private const val HEADER_BELOW = 8f
        private const val HEADER_SIZE = 13.5f
        private const val HEADER_TAG = "header"
        private const val LISTENER_NEEDED = "Notification listener required (step 4)"
        private const val PROFILES_ABOUT = "Each profile has its own Home: app order, hidden apps, folders, app names, " +
            "pinned inputs and wallpaper. Other settings apply to everyone. A child profile requires the PIN to switch profiles, " +
            "open settings or uninstall an app."
    }

    private lateinit var prefs: Prefs
    private lateinit var repo: AppRepo
    private lateinit var root: FrameLayout
    private lateinit var body: FrameLayout
    private lateinit var list: LinearLayout
    private lateinit var heading: TextView
    private lateinit var about: TextView
    private lateinit var icon: ImageView
    /** The home wallpaper, blurred and darkened ([loadBackdrop]). */
    private lateinit var backdrop: ImageView
    /** What [backdrop] shows ("source:file"), so a return from the wallpaper picker redoes it only if it changed. */
    @Volatile private var backdropKey: String? = null
    /** Pixels per unit of the 960-wide design: the screen's width / 960, whatever its density. */
    private var unit = 2f
    private lateinit var scroll: ScrollView
    private var page = Page.MAIN
    /** For each page, the row of its parent page that opened it, focused again on the way back. */
    private val openers = HashMap<Page, Int>()

    /**
     * The top level ([MAIN]) lists the groups, the entries with a [title], in this order; each group
     * page holds the rows, and the list pages (keep list, hidden apps…) sit one level below their group.
     */
    private enum class Page(val title: String? = null, val summary: String? = null) {
        MAIN,
        LOOK("Appearance", "Clock, Dock, profile photo, weather, selection effects, Control Center " +
            "(style and controls) and startup animation."),
        WALLPAPER("Wallpaper and Photo Frame", "Home wallpaper and how often it changes, " +
            "and the photo frame used as the TV's screensaver."),
        SHELF("Top Shelf", "Content from the selected Dock app, full-screen above the Dock, and its sources."),
        PLAYBACK("Playback and Notifications", "The Control Center player for media playing in apps, " +
            "and notification banners at the top right of the screen."),
        MEMORY("Memory and Power", "Free memory during sleep, at regular intervals or immediately; apps to keep, " +
            "and power button behavior."),
        APPS("Apps", "Apps hidden from Home, removed preinstalled apps and folders."),
        ACCOUNTS("Profiles", "A Home for each person, with their apps, folders and wallpaper; a child profile is protected by a PIN."),
        PHONE_GROUP("Phone Page", "A web page for a phone on the same Wi-Fi: remote with keyboard, photos and image links, " +
            "app installation, QR code and access protection."),
        REMOTE("Remote Control", "Assign remote buttons to canvasOS functions on Home: " +
            "single or long press; assign a button by pressing it."),
        SYSTEM("System", "Local debugging and ADB, the TV's address, Home protection, the TV's original settings, " +
            "canvasOS updates and version."),
        KEEP, HIDDEN, REMOVED, PHONE, LAST_CLEAN, BANNER_APPS, FOLDERS, PROFILES, REMOTE_ACTIONS, REMOTE_APPS, REMOTE_INPUTS;

        /** Where Back goes; null at the top level. */
        val parent: Page? get() = when (this) {
            MAIN -> null
            KEEP, LAST_CLEAN -> MEMORY
            HIDDEN, REMOVED, FOLDERS -> APPS
            PHONE -> PHONE_GROUP
            BANNER_APPS -> PLAYBACK
            PROFILES -> ACCOUNTS
            REMOTE_ACTIONS -> REMOTE
            REMOTE_APPS, REMOTE_INPUTS -> REMOTE_ACTIONS
            else -> MAIN
        }
    }

    /** The page this screen was opened on ([EXTRA_PAGE]); Back from it leaves, as it came from the home screen. */
    private var entry = Page.MAIN

    /** Sheets up over the list (profiles, the PIN), newest last. */
    private val sheets = ArrayList<Sheet>()
    /** The row focused when the first sheet went up, focused again once they are all gone. */
    private var sheetRow = 0
    /** In a restricted profile, until the PIN is given; the list stays hidden meanwhile. */
    private var locked = false
    private val pins by lazy { PinSheets(root, prefs) { showSheet(it) } }

    /** "Régénérer le code" was pressed while this screen was open. */
    private var tokenRenewed = false

    /** What "Libérer la mémoire" did while this screen was open, shown on its row. */
    private var cleanResult: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        repo = AppRepo(this)

        val m = resources.displayMetrics
        unit = maxOf(m.widthPixels, m.heightPixels) / 960f
        root = FrameLayout(this).apply { setBackgroundColor(BASE) }
        backdrop = ImageView(this).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
        root.addView(backdrop, FrameLayout.LayoutParams(-1, -1))
        heading = label(28f, TITLE, 600).apply { gravity = Gravity.CENTER }
        root.addView(heading, FrameLayout.LayoutParams(-1, -2, Gravity.TOP).apply { topMargin = u(TITLE_TOP) })

        // The left column is everything left of the list; the card and the description are centred in it.
        val column = 960f - LIST_END - LIST_WIDTH
        body = FrameLayout(this)
        icon = ImageView(this).apply {
            showIcon(this)
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipRounded(u(17).toFloat())
        }
        body.addView(icon, FrameLayout.LayoutParams(u(CARD_W), u(CARD_H), Gravity.TOP or Gravity.START).apply {
            leftMargin = u((column - CARD_W) / 2); topMargin = u(CARD_TOP)
        })
        about = label(15f, DESCRIPTION).apply { gravity = Gravity.CENTER_HORIZONTAL }
        // Low in the column as on tvOS; a description too long for the space left rises towards the card.
        about.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val over = v.bottom - (root.height - u(24))
            v.translationY = -minOf(maxOf(over, 0), maxOf(v.top - u(CARD_TOP + CARD_H + 18), 0)).toFloat()
        }
        body.addView(about, FrameLayout.LayoutParams(u(ABOUT_W), -2, Gravity.TOP or Gravity.START).apply {
            leftMargin = u((column - ABOUT_W) / 2); topMargin = u(ABOUT_TOP)
        })

        // Not clipped, so a focused row can grow past the list's edges; the top padding (set per page in
        // [show]) lines the first row up with the card, and the rows scroll up under a fade below the title.
        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            clipChildren = false
            clipToPadding = false
            setPadding(u(10), 0, u(10), u(48))
        }
        scroll = ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            clipToPadding = false
            clipChildren = false
            // A fading edge redraws through an offscreen layer while scrolling: not on weak TVs.
            if (!Device.isLowEnd(this@SettingsActivity)) {
                isVerticalFadingEdgeEnabled = true
                setFadingEdgeLength(u(26))
            }
            addView(list)
        }
        body.addView(scroll, FrameLayout.LayoutParams(u(LIST_WIDTH + 20), -1, Gravity.TOP or Gravity.END).apply {
            rightMargin = u(LIST_END - 10); topMargin = u(LIST_TOP)
        })
        root.addView(body, FrameLayout.LayoutParams(-1, -1))
        setContentView(root)
        entry = when (intent.getStringExtra(EXTRA_PAGE)) { PAGE_PROFILES -> Page.PROFILES; PAGE_LOOK -> Page.LOOK; else -> Page.MAIN }
        // A restricted profile reaches the settings only with the PIN, whichever way they were opened
        // (the avatar, the Control Center's gear, the grid's tile, another launcher).
        if (prefs.restricted && prefs.hasPin) {
            locked = true
            body.visibility = android.view.View.INVISIBLE
            heading.text = "Home Settings"
            pins.ask("Home Settings") {
                locked = false
                body.visibility = android.view.View.VISIBLE
                show(entry)
            }
        } else show(entry)
    }

    /**
     * Shows [s] over the page and keeps focus off the rows behind it. Once the last sheet closes,
     * the page is drawn again (what the sheets changed shows) with focus back on its row; a PIN
     * never given leaves the screen.
     */
    private fun showSheet(s: Sheet) {
        if (sheets.isEmpty()) sheetRow = list.indexOfChild(list.focusedChild).coerceAtLeast(0)
        sheets += s
        scroll.descendantFocusability = android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
        s.onDismiss {
            sheets -= s
            if (sheets.isNotEmpty() || isDestroyed) return@onDismiss
            if (locked) { finish(); return@onDismiss }
            scroll.descendantFocusability = android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS
            show(page, sheetRow)
        }
        s.show()
    }

    private fun show(p: Page, focusIndex: Int = 0) {
        // Another page starts at its top, its first header showing; the same page drawn again stays where it was.
        if (p != page) scroll.scrollTo(0, 0)
        page = p
        list.removeAllViews()
        when (p) {
            Page.MAIN -> main()
            Page.LOOK -> lookGroup()
            Page.WALLPAPER -> wallpaperGroup()
            Page.SHELF -> shelfGroup()
            Page.PLAYBACK -> playbackGroup()
            Page.MEMORY -> memoryGroup()
            Page.APPS -> appsGroup()
            Page.ACCOUNTS -> accountsGroup()
            Page.PHONE_GROUP -> phoneGroup()
            Page.SYSTEM -> systemGroup()
            Page.REMOTE -> remoteGroup()
            Page.REMOTE_ACTIONS -> remoteActions()
            Page.REMOTE_APPS -> remoteApps()
            Page.REMOTE_INPUTS -> remoteInputs()
            Page.KEEP -> appPicker("Apps to Keep", prefs.keep) { pkg, on ->
                prefs.keep = if (on) prefs.keep + pkg else prefs.keep - pkg
            }
            Page.HIDDEN -> appPicker("Hidden Apps", prefs.hidden, includeHidden = true) { pkg, on ->
                prefs.hidden = if (on) prefs.hidden + pkg else prefs.hidden - pkg
            }
            Page.REMOVED -> removedApps()
            Page.PHONE -> phonePage()
            Page.LAST_CLEAN -> lastCleanPage()
            Page.BANNER_APPS -> bannerAppsPage()
            Page.FOLDERS -> foldersPage()
            Page.PROFILES -> profilesPage()
        }
        // A list shorter than the column is centred in it; a longer one starts at the top and scrolls.
        list.setPadding(list.paddingLeft, u(8), list.paddingRight, u(48))
        list.post { centreList() }
        p.title?.let {
            heading.text = it
            // Under the icon until a row with a description of its own is focused.
            about.text = p.summary
        }
        pageIcon(p)
        list.post {
            val target = list.getChildAt(focusIndex.coerceIn(0, maxOf(list.childCount - 1, 0)))
            (target?.takeIf { it.isFocusable } ?: (0 until list.childCount).map { list.getChildAt(it) }.firstOrNull { it.isFocusable })
                ?.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        Foreground.resumed(this)
        loadBackdrop()
    }

    override fun onPause() {
        // A lesson left half way (the screen went away) must not keep every key of the TV.
        KeyCapture.learning = null
        learning = null
        Foreground.paused(this)
        super.onPause()
    }

    /**
     * The home screen's wallpaper behind the page, as Apple TV's settings sit on a blurred, darkened
     * haze of the home screen: the current picture of the current source decoded small, blurred twice
     * over, darkened, and its highlights held down so white text stays legible on a bright photo.
     * A gradient theme is blurred the same way; no picture yet leaves the plain dark page. Done off
     * the main thread, again on return only if the wallpaper changed meanwhile.
     */
    private fun loadBackdrop() {
        val theme = prefs.background
        Thread {
            // An earlier version let the phone page upload a settings background; it is no longer used.
            listOfNotNull(filesDir, getExternalFilesDir(null)).forEach { java.io.File(it, "settings_bg.jpg").delete() }
            val photo = if (Wallpapers.isPhoto(theme)) Wallpapers(applicationContext, Wallpapers.sourceFor(theme)).current() else null
            val key = "$theme:${photo?.name}"
            if (key == backdropKey) return@Thread
            val src = runCatching {
                when {
                    photo != null -> decodeIcon(photo, 192)
                    Wallpapers.isPhoto(theme) -> null
                    else -> Backgrounds.render(theme)
                }
            }.getOrNull()
            val bmp = src?.let { settle(Wallpapers.blurred(Wallpapers.blurred(it, 0f), 0.55f)) }
            runOnUiThread {
                if (isDestroyed) return@runOnUiThread
                val first = backdropKey == null
                backdropKey = key
                backdrop.setImageBitmap(bmp)
                if (first && bmp != null) {
                    backdrop.alpha = 0f
                    backdrop.animate().alpha(1f).setDuration(220).start()
                }
            }
        }.start()
    }

    /** Scales down every pixel brighter than [MAX_LUMA], keeping its hue: a white sky turns the deep grey-blue of tvOS's page. */
    private fun settle(b: android.graphics.Bitmap): android.graphics.Bitmap {
        val w = b.width
        val h = b.height
        val px = IntArray(w * h)
        b.getPixels(px, 0, w, 0, 0, w, h)
        for (i in px.indices) {
            val c = px[i]
            val r = Color.red(c); val g = Color.green(c); val bl = Color.blue(c)
            val luma = 0.299f * r + 0.587f * g + 0.114f * bl
            if (luma <= MAX_LUMA) continue
            val k = MAX_LUMA / luma
            px[i] = Color.rgb((r * k).toInt(), (g * k).toInt(), (bl * k).toInt())
        }
        b.setPixels(px, 0, w, 0, 0, w, h)
        return b
    }

    /** [v] units of the 960-wide design in pixels. */
    /** Centres the rows vertically when they do not fill the column; otherwise they start under the title. */
    private fun centreList() {
        val content = (0 until list.childCount).sumOf { i ->
            val v = list.getChildAt(i); val lp = v.layoutParams as LinearLayout.LayoutParams
            v.measuredHeight + lp.topMargin + lp.bottomMargin
        }
        val room = scroll.height - u(8) - u(48)
        val top = u(8) + maxOf(0, (room - content) / 2)
        if (list.paddingTop != top) list.setPadding(list.paddingLeft, top, list.paddingRight, list.paddingBottom)
    }

    private fun u(v: Number): Int = (v.toFloat() * unit + 0.5f).toInt()

    /** A text sized in design units rather than sp, so the page keeps Apple TV's proportions at any density. */
    private fun label(size: Float, color: Int, weight: Int = 400): TextView =
        text(size, color, weight = weight).apply { setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, size * unit) }

    /** Whether the big icon shows the current profile's avatar ([pageIcon]) rather than the settings icon. */
    private var iconAvatar = false

    /** On the profiles page the big icon is the current profile's avatar, as on tvOS's account screens; elsewhere the settings icon. */
    private fun pageIcon(p: Page) {
        if (p == Page.PROFILES) {
            icon.setImageDrawable(ProfileAvatar.drawable(this, prefs.current(), (u(BIG_AVATAR) / resources.displayMetrics.density).toInt()))
            icon.scaleType = ImageView.ScaleType.CENTER
            iconAvatar = true
        } else if (iconAvatar) {
            iconAvatar = false
            icon.scaleType = ImageView.ScaleType.CENTER_CROP
            showIcon(icon)
        }
    }

    /** Opens [p] from a row of the current page, which Back from [p] focuses again. */
    private fun open(p: Page) {
        openers[p] = list.indexOfChild(list.focusedChild).coerceAtLeast(0)
        show(p)
    }

    private fun showIcon(v: ImageView) {
        val own = iconSource(this)?.let { decodeIcon(it, u(CARD_W * 2)) }
        if (own != null) v.setImageBitmap(own) else v.setImageResource(R.drawable.banner_settings)
    }

    /** The top level: one row per group ([Page.title]), each opening its page. */
    private fun main() {
        heading.text = "Home Settings"
        about.text = ""
        for (g in Page.values()) g.title?.let { t -> row(t, g.summary, { "" }, chevron = true) { open(g) } }
    }

    /** Apparence: the clock, the home screen, the weather, effects and the boot animation. */
    private fun lookGroup() {
        section("Clock")
        row("Time Format", "Time on Home and in Control Center, in 12-hour (11:49 AM) or 24-hour (23:49) format.",
            { if (prefs.clock24) "24 h" else "12 h" }) { prefs.clock24 = !prefs.clock24 }
        row("Clock", "A small clock at the top right, a large clock with date at the top left (classic style), or none.", {
            when (prefs.clockStyle) { Prefs.CLOCK_LARGE -> "Large, Left"; Prefs.CLOCK_OFF -> "Hidden"; else -> "Top Right" }
        }) {
            val opts = listOf(Prefs.CLOCK_CORNER, Prefs.CLOCK_LARGE, Prefs.CLOCK_OFF)
            prefs.clockStyle = opts[(opts.indexOf(prefs.clockStyle) + 1) % opts.size]
        }
        section("Home")
        row("Dock", "Recent: the app you open moves to the front. Fixed: keeps the order chosen with “Edit Home”.",
            { if (prefs.dockRecents) "Recent" else "Fixed" }) { prefs.dockRecents = !prefs.dockRecents }
        row("Profile Photo", "The profile photo at the top right of Home: an initial on the profile's color, or a photo uploaded " +
            "from the phone page. Press it to switch profiles. It stays visible while multiple profiles exist.", { onOff(prefs.showAvatar) }) { prefs.showAvatar = !prefs.showAvatar }
        section("Weather")
        row("Weather", "Temperature and weather beside the clock, and a five-day forecast in Control Center. " +
            "Forecasts come from Open-Meteo and refresh every 30 minutes while Home is visible. " +
            "Until you choose a city, the location is estimated once from the TV's public IP address (ip-api.com).",
            { onOff(prefs.weather) }) { prefs.weather = !prefs.weather }
        row("Weather Location", "Choose another city from the phone page (Settings tab, Weather section), or a computer using CONFIG (weather_city).",
            { Weather.placeLabel(prefs).ifEmpty { "Not set yet" } }) {}
        section("Effects")
        row("Selection Effect", "tvOS: the selected app grows, tilts with movement and reflects light. Simple: it only grows." +
            "\nAuto chooses Simple on less powerful TVs.", {
            auto(prefs.focusStyle, prefs.focusTvos(this), "tvOS", "Simple")
        }) {
            prefs.focusStyle = next(prefs.focusStyle, Prefs.FOCUS_TVOS, Prefs.FOCUS_SIMPLE)
        }
        section("Control Center")
        row("Control Center Style", "Glass: frosted glass showing the image behind it; the selected button turns white. Panel: the classic dark panel." +
            "\nAuto chooses Glass on all TVs.", {
            auto(prefs.ccStyle, prefs.ccGlass(this), "Glass", "Panel")
        }) {
            prefs.ccStyle = next(prefs.ccStyle, Prefs.CC_GLASS, Prefs.CC_PANEL)
        }
        row("Customize Control Center", "Opens Control Center in edit mode. Its controls occupy a grid " +
            "of 4 × 6 cells across up to three pages (Down from the last row goes to the next page). Press OK on a control to " +
            "move, resize or remove it, or add a control or page. Back to finish. " +
            "You can also hold OK on the Control Center button at the top right of Home.", { "" }, chevron = true) {
            startActivity(Intent(this, HomeActivity::class.java).putExtra(HomeActivity.EXTRA_EDIT_CC, true))
        }
        row("Reset Layout", "Returns Control Center controls to their original positions and sizes across two pages.", { "" }) {
            showSheet(Sheet(root).title("Reset Control Center?")
                .message("Controls return to their original positions and sizes across two pages.")
                .button("Reset") { prefs.resetCcLayout() }
                .button("Cancel") {})
        }
        section("Logo")
        row("Logo", "Built-in title card and startup film: canvasOS or Apple TV (tvOS). Used for the settings icon and startup animation, unless changed below.", {
            Brand.label(prefs.brand)
        }) {
            prefs.brand = if (prefs.brand == Brand.TVOS) Brand.CANVASOS else Brand.TVOS
            showIcon(icon)
        }
        val picture = iconFile(this) != null
        row("Settings Icon", "The settings icon on Home and at the top of this page: the selected logo's title card, your uploaded image from the phone page, or the built-in gear." +
            if (picture) "" else "\nNo image uploaded yet.", {
            when (prefs.settingsIcon) { Prefs.ICON_OWN -> "Uploaded Image"; Prefs.ICON_GEAR -> "Gear"; else -> "Logo " + Brand.label(prefs.brand) }
        }) {
            val opts = listOfNotNull(Prefs.ICON_BRAND, Prefs.ICON_OWN.takeIf { picture }, Prefs.ICON_GEAR)
            prefs.settingsIcon = opts[(opts.indexOf(prefs.settingsIcon) + 1) % opts.size]
            showIcon(icon)
        }
        section("Startup Animation")
        val video = Intro.videoFile(this) != null
        row("Startup Animation", "Plays the first time Home opens after startup; the logo grows and opens Home at the end. Press any button to skip.", {
            when (prefs.intro) { Prefs.INTRO_BOOT -> "At Startup"; Prefs.INTRO_WAKE -> "Startup and Wake"; else -> "Off" }
        }) {
            val opts = listOf(Prefs.INTRO_OFF, Prefs.INTRO_BOOT, Prefs.INTRO_WAKE)
            prefs.intro = opts[(opts.indexOf(prefs.intro) + 1) % opts.size]
            // This boot counts as played: turning it on doesn't start it on the next return home.
            prefs.introBoot = Intro.bootStamp(this)
        }
        row("Animation Content", "Selected logo film: canvasOS or Apple TV, according to the Logo setting. Uploaded video: yours, from the phone page, whose final logo grows and opens Home. canvasOS Name: the name fades in and grows." +
            if (video) "" else "\nNo video uploaded yet: the selected logo film plays instead.", {
            when (prefs.introSource) { Prefs.INTRO_VIDEO -> "Uploaded Video"; Prefs.INTRO_WORDMARK -> "canvasOS Name"; else -> "Film " + Brand.label(prefs.brand) }
        }) {
            val opts = listOfNotNull(Prefs.INTRO_BRAND, Prefs.INTRO_VIDEO.takeIf { video }, Prefs.INTRO_WORDMARK)
            prefs.introSource = opts[(opts.indexOf(prefs.introSource) + 1) % opts.size]
        }
        row("Wake Delay", "After waking, the screen stays black for this duration before the animation. Many TVs (especially OLED) take a few seconds to show an image: increase the delay if the start is missed, decrease it if the black screen lasts too long.", {
            if (prefs.wakeDelay == 0) "None" else "${prefs.wakeDelay / 1000.0} s".replace(".0 ", " ")
        }) {
            val opts = listOf(0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 10_000)
            prefs.wakeDelay = opts.firstOrNull { it > prefs.wakeDelay } ?: 0
        }
        row("Animation Sound", "Plays the animation film or video's sound.", { onOff(prefs.introSound) }) { prefs.introSound = !prefs.introSound }
        row("Preview Animation", "Returns to Home and plays it once now.", { "" }, chevron = true) {
            startActivity(Intent(this, HomeActivity::class.java)
                .putExtra(Intro.EXTRA_PREVIEW, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    /** Fond d'écran et cadre photo. */
    private fun wallpaperGroup() {
        section("Wallpaper")
        row("Wallpaper", "The Home background: a theme, wallpaper source or your own photos.", {
            Backgrounds.THEMES[prefs.background]?.first ?: Wallpapers.label(Wallpapers.sourceFor(prefs.background)).let {
                if (prefs.background == Wallpapers.MINE) it else "$it (auto)"
            }
        }, chevron = true) { startActivity(Intent(this, WallpaperActivity::class.java)) }
        row("Change Photo", "How often automatic wallpaper changes. Never: the photo changes only with Next Photo in Control Center (or Change Now below).",
            { minutesLabel(prefs.wallMinutes) }) {
            val opts = listOf(0, 15, 30, 60, 180, 1440)
            prefs.wallMinutes = opts[(opts.indexOf(prefs.wallMinutes) + 1) % opts.size]
        }
        row("Change Now", "Gets a new photo from the wallpaper source. Photos download when displayed; old photos are deleted after a few days.",
            { "${Wallpapers(this, Wallpapers.sourceFor(prefs.background)).files().size} photos" }) { v ->
            v.text = "Downloading…"
            Thread {
                val w = Wallpapers(applicationContext, Wallpapers.sourceFor(prefs.background))
                w.refresh()
                w.advance(prefs.wallMinutes, force = true)
                runOnUiThread { v.text = "${w.files().size} photos" }
            }.start()
        }
        section("Photo Frame")
        row("Photo Frame as Screensaver", "After inactivity, the TV shows full-screen photos or aerial videos instead of its own screensaver.",
            { onOff(prefs.frameScreensaver) }) { v ->
            val on = !prefs.frameScreensaver
            val current = Settings.Secure.getString(contentResolver, "screensaver_components").orEmpty()
            val mine = "$packageName/${PhotoDream::class.java.name}"
            // Whatever the TV had before goes back when the frame is turned off (Sony's own if unknown).
            if (on && !current.startsWith(packageName)) prefs.prevScreensaver = current
            val comp = if (on) mine else prefs.prevScreensaver.ifEmpty { SONY_DREAM }
            v.text = "Applying…"
            Thread {
                val ok = putSecure("screensaver_components", comp) && putSecure("screensaver_enabled", "1")
                if (ok) prefs.frameScreensaver = on
                runOnUiThread { v.text = if (ok) onOff(prefs.frameScreensaver) else "Local debugging required" }
            }.start()
        }
        row("Screensaver Content", "The photo frame shows wallpaper photos or Apple TV aerial videos.",
            { if (prefs.saverMode == Prefs.SAVER_AERIAL) "Aerial Videos" else "Photos" }) {
            prefs.saverMode = if (prefs.saverMode == Prefs.SAVER_AERIAL) Prefs.SAVER_PHOTOS else Prefs.SAVER_AERIAL
        }
        row("Photo Frame Delay", "Idle time before the photo frame appears.", { minutesLabel(idleMinutes()) }) { v ->
            val opts = listOf(1, 3, 5, 10, 15, 30)
            val next = opts[(opts.indexOf(idleMinutes()) + 1) % opts.size]
            v.text = "Applying…"
            Thread {
                val ok = LocalAdb.shell(applicationContext, "settings put system screen_off_timeout ${next * 60_000}") != null
                runOnUiThread { v.text = if (ok) minutesLabel(next) else "Local debugging required" }
            }.start()
        }
        row("Time per Photo", "How long each photo appears in the photo frame.", { "${prefs.frameSeconds} s" }) {
            val opts = listOf(15, 30, 60, 120)
            prefs.frameSeconds = opts[(opts.indexOf(prefs.frameSeconds) + 1) % opts.size]
        }
    }

    /** Top Shelf: its mode and its Android TV source. */
    private fun shelfGroup() {
        section("Top Shelf")
        row("Top Shelf", "Content from the selected Dock app, full-screen above it. Full: the “Continue Watching” row " +
            "published for Android TV, then TMDB trends (with your key) and web sources. Android TV Only: no TMDB or web. " +
            "Off: the wallpaper stays visible.", {
            when (prefs.shelfMode) { Prefs.SHELF_TV -> "Android TV Only"; Prefs.SHELF_OFF -> "Off"; else -> "Full" }
        }) {
            prefs.shelfMode = Prefs.SHELF_MODES[(Prefs.SHELF_MODES.indexOf(prefs.shelfMode) + 1) % Prefs.SHELF_MODES.size]
        }
        section("Sources")
        row("Continue Watching", "The row that apps (YouTube, Netflix…) publish for Android TV: resume what you were watching. " +
            "When off, Top Shelf uses only TMDB and web sources, when allowed by the Top Shelf mode.",
            { onOff(prefs.watchNext) }) { prefs.watchNext = !prefs.watchNext }
    }

    // ---------------------------------------------------------------- Télécommande

    /** The binding being changed (its key kept for a new function, its function for a new key); null for a new one. */
    private var remoteEdit: KeyMap.Binding? = null
    /** The key being learnt: the function it will do, the binding it replaces, and the sheet asking for it. */
    private class Learning(val action: String, val replacing: KeyMap.Binding?, val sheet: Sheet)
    private var learning: Learning? = null
    private var learnCode = -1
    private val tv by lazy { TvControls(this) }

    private fun bindings() = KeyMap.parse(prefs.keyMap)

    /** The name of an app or an input bound to a key, for the list. */
    private fun boundName(action: String): String? = when {
        action.startsWith(KeyMap.APP) -> repo.allApps().firstOrNull { it.pkg == action.removePrefix(KeyMap.APP) }?.label
        action.startsWith(KeyMap.INPUT) -> InputTile.decode(action.removePrefix(KeyMap.INPUT))?.name
        else -> null
    }

    /** Télécommande: the shortcuts, one row each, and a new one. */
    private fun remoteGroup() {
        val list = bindings()
        section("Shortcuts")
        if (list.isEmpty()) row("No shortcuts", "Choose “Add a Shortcut…”, select a function, then press the remote button to assign it.", { "" }) {}
        for (b in list) {
            row(KeyMap.keyName(b.code, b.long), "Press to change the button or function, or delete this shortcut. " +
                "Shortcuts work on Home, not in other apps.", { KeyMap.label(b.action, ::boundName) }) { bindingMenu(b) }
        }
        row("Add a Shortcut…", "Choose the function, then the button: a short press assigns a single press; holding assigns a long press. " +
            "TV buttons (Home, Volume) and Home controls (arrows, OK, single Back press) cannot be assigned.",
            { "" }, chevron = true) { remoteEdit = null; open(Page.REMOTE_ACTIONS) }
        section("TV Buttons")
        row("Capture TV Buttons", "Off by default. Settings and Profile buttons (and others such as Guide, Input and TV) " +
            "are intercepted by the TV before reaching canvasOS. When on, the canvasOS accessibility service (used by Home Protection) " +
            "filters buttons: it sees every button pressed in all apps, but handles these buttons only on Home and " +
            "only when assigned; everything else passes through. Nothing is recorded. Requires local debugging once.",
            { if (!prefs.captureKeys) "Off" else if (HomeGuardService.isRunning) "On" else "On (service inactive)" }) { v ->
            val on = !prefs.captureKeys
            v.text = "Applying…"
            Thread {
                val ok = (Guard.hasSecureSettings(this) || Guard.grantViaLocalAdb(applicationContext)) && Guard.setCaptureKeys(applicationContext, on)
                runOnUiThread { v.text = if (!ok) "Local debugging required" else if (on) "On" else "Off" }
            }.start()
        }
    }

    private fun bindingMenu(b: KeyMap.Binding) {
        showSheet(Sheet(root).title(KeyMap.keyName(b.code, b.long)).message(KeyMap.label(b.action, ::boundName))
            .button("Change Button") { learn(b.action, replacing = b) }
            .button("Change Function") { remoteEdit = b; open(Page.REMOTE_ACTIONS) }
            .button("Delete") { prefs.keyMap = KeyMap.serialize(KeyMap.remove(bindings(), b)) }
            .button("Cancel") {})
    }

    /** The functions a key can do, then the apps and the inputs on pages of their own. */
    private fun remoteActions() {
        heading.text = "Function"
        about.text = if (remoteEdit != null) "The new function for “${KeyMap.keyName(remoteEdit!!.code, remoteEdit!!.long)}”." else "What the button will do; you will choose the button next."
        for ((id, name) in KeyMap.ACTIONS) row(name, null, { "" }) { chooseAction(id) }
        row("Open an App…", null, { "" }, chevron = true) { open(Page.REMOTE_APPS) }
        row("HDMI Input…", null, { "" }, chevron = true) { open(Page.REMOTE_INPUTS) }
    }

    private fun remoteApps() {
        heading.text = "Open an App"
        about.text = "The app this button will open."
        for (a in repo.allApps().sortedBy { it.label.lowercase() }) row(a.label, null, { "" }) { chooseAction(KeyMap.APP + a.pkg) }
    }

    /** The TV's inputs, read off the UI thread (a few hundred ms on some firmwares). */
    private fun remoteInputs() {
        heading.text = "HDMI Input"
        about.text = "The input this button will switch to."
        row("Finding inputs…", null, { "" }) {}
        Thread {
            val inputs = runCatching { tv.inputs() }.getOrDefault(emptyList())
            runOnUiThread {
                if (page != Page.REMOTE_INPUTS || isDestroyed) return@runOnUiThread
                list.removeAllViews()
                if (inputs.isEmpty()) row("No inputs", "This TV does not expose HDMI inputs to canvasOS (see AGENTS.md, section 9).", { "" }) {}
                for (i in inputs) row(i.name, null, { if (i.connected) "Connected" else "" }) { chooseAction(KeyMap.INPUT + InputTile.encode(i)) }
                list.post { centreList(); list.getChildAt(0)?.requestFocus() }
            }
        }.start()
    }

    /** A function chosen: the key being changed keeps it, a new shortcut asks for its key. */
    private fun chooseAction(action: String) {
        val e = remoteEdit
        if (e != null) {
            remoteEdit = null
            save(KeyMap.Binding(e.code, e.long, action), replacing = e)
            show(Page.REMOTE)
        } else learn(action, replacing = null)
    }

    /** Asks for the key: the next key pressed is read in [dispatchKeyEvent], Back cancels. */
    private fun learn(action: String, replacing: KeyMap.Binding?) {
        val sheet = Sheet(root).title("Press a Button")
            .message("A short press assigns a single press; holding assigns a long press. Back cancels." +
                if (prefs.captureKeys) "" else " Settings and Profile buttons require “Capture TV Buttons”.")
        learning = Learning(action, replacing, sheet)
        learnCode = -1
        // With key capture on, the service hands every key here, those the TV would keep included.
        KeyCapture.learning = { e -> dispatchKeyEvent(e) }
        // Only Back (dispatchKeyEvent) or a key learnt dismiss it; both clear the lesson first.
        showSheet(sheet)
    }

    /** A key pressed ([long]: held) while learning: refused with the reason, confirmed when the key already does something, else bound. */
    private fun learnt(code: Int, long: Boolean) {
        val l = learning ?: return
        KeyMap.reserved(code, long)?.let { l.sheet.message("$it Try another button, or Back to cancel."); return }
        val b = KeyMap.Binding(code, long, l.action)
        val taken = KeyMap.find(bindings(), code, long)?.takeIf { l.replacing == null || !it.sameKey(l.replacing.code, l.replacing.long) }
        learning = null
        KeyCapture.learning = null
        page = Page.REMOTE
        l.sheet.dismiss()
        if (taken != null) {
            showSheet(Sheet(root).title("Reassign “${KeyMap.keyName(code, long)}”?")
                .message("This button already performs “${KeyMap.label(taken.action, ::boundName)}”.")
                .button("Reassign") { save(b, l.replacing) }
                .button("Cancel") {})
        } else save(b, l.replacing)
    }

    private fun save(b: KeyMap.Binding, replacing: KeyMap.Binding?) {
        var l = bindings()
        if (replacing != null) l = KeyMap.remove(l, replacing)
        prefs.keyMap = KeyMap.serialize(KeyMap.put(l, b))
    }

    /**
     * While a key is being learnt, every key is the lesson's: Back cancels, a held key binds its
     * long press at once, any other key binds its press on release. Nothing reaches the rows behind.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val l = learning ?: return super.dispatchKeyEvent(event)
        val code = event.keyCode
        if (code == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) { learning = null; KeyCapture.learning = null; l.sheet.dismiss() }
            return true
        }
        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.repeatCount == 0) learnCode = code
                if (event.isLongPress && learnCode == code) { learnCode = -1; learnt(code, long = true) }
            }
            KeyEvent.ACTION_UP -> if (learnCode == code) { learnCode = -1; learnt(code, long = false) }
        }
        return true
    }

    /** Lecture et notifications: the Control Center player and the banners. */
    private fun playbackGroup() {
        section("Playback")
        row("Now Playing", "On by default. Shows what an app is playing in Control Center (title, artist, artwork), with play/pause and next track. " +
            "Reads apps' playback information (title, artist, artwork, playback state) through the notification listener; nothing is recorded.",
            { if (!MediaSessions.isAvailable(this)) LISTENER_NEEDED else onOff(prefs.nowPlaying) }) { v ->
            // On by default: without the listener it shows as needing it, and OK then asks for it rather than switching off.
            if (prefs.nowPlaying && MediaSessions.isAvailable(this)) { prefs.nowPlaying = false; return@row }
            v.text = "Applying…"
            Thread {
                val ok = MediaSessions.isAvailable(applicationContext) || (allowListener() && MediaSessions.isAvailable(applicationContext))
                if (ok) prefs.nowPlaying = true
                runOnUiThread { v.text = if (ok) onOff(true) else LISTENER_NEEDED }
            }.start()
        }
        row("Home Player", "On by default with Now Playing. A capsule at the top left of Home shows the artwork, title and artist " +
            "of what an app is playing, without controls: OK opens that app; playback controls stay in Control Center. " +
            "No additional information is read.",
            { if (!prefs.nowPlaying) "Now Playing required" else onOff(prefs.homePlayer) }) { v ->
            if (!prefs.nowPlaying) { Motion.bump(v, 1, 0); return@row }
            prefs.homePlayer = !prefs.homePlayer
        }
        row("Lyrics", "On by default. For each new track, the TV asks LRCLIB (lrclib.net, free, no key) for lyrics: " +
            "the track title, artist, album and duration are sent, nothing else, and nothing is recorded. When available, a Lyrics button appears beside the " +
            "Home player when selected: OK shows synced lyrics over the darkened wallpaper (unsynced lyrics are not offered); OK again hides them. " +
            "When off, nothing is sent to LRCLIB.",
            { if (!prefs.nowPlaying || !prefs.homePlayer) "Home Player required" else onOff(prefs.lyrics) }) { v ->
            if (!prefs.nowPlaying || !prefs.homePlayer) { Motion.bump(v, 1, 0); return@row }
            prefs.lyrics = !prefs.lyrics
        }
        row("Lyrics Position", "Where lyrics appear on Home: left below the player, center or right.", {
            when (prefs.lyricsAlign) { Prefs.LYRICS_CENTER -> "Center"; Prefs.LYRICS_RIGHT -> "Right"; else -> "Left" }
        }) { prefs.lyricsAlign = Prefs.LYRICS_ALIGNS[(Prefs.LYRICS_ALIGNS.indexOf(prefs.lyricsAlign) + 1) % Prefs.LYRICS_ALIGNS.size] }
        section("Notifications")
        row("Notification Banners", "On by default. Shows app notifications at the top right (doorbell, messages, home automation…). " +
            "Notification titles and text from all apps except excluded ones are read, displayed for 6 seconds and never recorded. " +
            "When off, no notifications are read.",
            { if (!Banners.listenerGranted(this)) LISTENER_NEEDED else onOff(prefs.banners) }) { v ->
            if (prefs.banners && Banners.listenerGranted(this)) {
                prefs.banners = false
                show(Page.PLAYBACK, list.indexOfChild(list.focusedChild).coerceAtLeast(0))
                return@row
            }
            v.text = "Applying…"
            Thread {
                val ok = Banners.listenerGranted(applicationContext) || allowListener()
                // Over the app being watched, banners need the overlay permission the guard also uses.
                if (ok && !Settings.canDrawOverlays(applicationContext) && prefs.adbAuthorized)
                    LocalAdb.shell(applicationContext, "appops set $packageName SYSTEM_ALERT_WINDOW allow", 10_000)
                if (ok) prefs.banners = true
                runOnUiThread {
                    if (!ok) v.text = LISTENER_NEEDED
                    // "Apps exclues des bannières" appears below: the page is rebuilt where the focus is.
                    else if (page == Page.PLAYBACK && !isDestroyed) show(Page.PLAYBACK, list.indexOfChild(list.focusedChild).coerceAtLeast(0))
                }
            }.start()
        }
        if (prefs.banners) row("Apps Excluded from Banners", "Notifications from selected apps are neither read nor displayed. All others appear as banners." +
            if (Settings.canDrawOverlays(this)) "" else "\nWithout permission to display over other apps (step 4), banners appear only on Home.",
            { "${prefs.bannerExcluded.size}" }, chevron = true) { open(Page.BANNER_APPS) }
    }

    /** Mémoire et alimentation. */
    private fun memoryGroup() {
        section("Cleanup")
        row("Free Memory During Sleep", "Closes background apps when the TV sleeps, for a smoother wake. " +
            "Apps playing audio or video, or running a service (casting, server), are kept.", { onOff(prefs.cleanOnStandby) }) {
            prefs.cleanOnStandby = !prefs.cleanOnStandby
        }
        row("Scheduled Cleanup", "Regularly closes background apps, except the on-screen app and those playing audio or video or running a service.", {
            if (prefs.cleanIntervalHours == 0) "Off" else "Every ${prefs.cleanIntervalHours} h"
        }) {
            val opts = listOf(0, 2, 4, 8)
            prefs.cleanIntervalHours = opts[(opts.indexOf(prefs.cleanIntervalHours) + 1) % opts.size]
            KeeperService.start(this, KeeperService.ACTION_RESCHEDULE)
        }
        row("Free Memory", "Closes background apps now. Apps playing audio or video or running a service " +
            "(casting, server) are kept.", { cleanResult ?: "${Cleaner.availMb(this)} MB free" }) { v ->
            v.text = "Freeing…"
            Thread {
                val r = Cleaner.clean(applicationContext)
                runOnUiThread {
                    // Short: the label must keep its room. The whole summary is on "Dernier nettoyage".
                    cleanResult = if (r.viaAdb) "${r.freedMb} MB · ${r.closedText}" else r.freedText
                    // "Dernier nettoyage" below changed too, so the page is rebuilt (where the focus is) rather than one value set.
                    if (page == Page.MEMORY && !isDestroyed) show(Page.MEMORY, list.indexOfChild(list.focusedChild).coerceAtLeast(0))
                }
            }.start()
        }
        row("Last Cleanup", if (prefs.lastClean.isEmpty()) "Memory has not been freed yet." else "${lastCleanWhat()}.\nPress to see closed apps.",
            { lastCleanWhen() }, chevron = true) { open(Page.LAST_CLEAN) }
        row("Apps to Keep", "Apps that memory cleanup never closes, such as screen casting. " +
            "Apps playing audio or video or running a service are always kept while active.", { "${prefs.keep.size}" }, chevron = true) {
            open(Page.KEEP)
        }
        section("Power")
        row("Power Button", "Sleep: normal behavior. Full Shutdown: saves power, but startup takes longer.", {
            if (prefs.shutdownOnPowerKey) "Full Shutdown" else "Sleep"
        }) {
            prefs.shutdownOnPowerKey = !prefs.shutdownOnPowerKey
            if (prefs.shutdownOnPowerKey && !prefs.adbAuthorized)
                Toast.makeText(this, "Allow local debugging first to enable this", Toast.LENGTH_LONG).show()
        }
    }

    /** Apps: hidden and removed apps, folders. */
    private fun appsGroup() {
        section("Apps")
        row("Hidden Apps", "Apps that do not appear on Home.", { "${prefs.hidden.size}" }, chevron = true) { open(Page.HIDDEN) }
        row("Removed Preinstalled Apps", "Preinstalled apps removed from the long-press menu; restore them here.", { "${prefs.removed.size}" }, chevron = true) {
            open(Page.REMOVED)
        }
        section("Folders")
        row("Folders", "Home folders and their apps. Create and manage them on Home by holding an app or folder.",
            { "${prefs.folderList().size}" }, chevron = true) { open(Page.FOLDERS) }
    }

    /** Profils. */
    private fun accountsGroup() {
        section("Profiles")
        row("Current Profile", "The profile whose Home is displayed. Switch using the profile photo at the top right of Home.",
            { Profiles.displayName(prefs.current()) }) {}
        row("Manage Profiles", PROFILES_ABOUT, { "${prefs.profileList().size}" }, chevron = true) { open(Page.PROFILES) }
    }

    /** Page téléphone. */
    private fun phoneGroup() {
        section("Access")
        row("Show QR Code", "Scan with a phone on the same Wi-Fi. The page works as a remote with keyboard, uploads photos and image links, controls startup animation " +
            "and weather, and installs apps (APKs). Remote control and app installation require local debugging.",
            { "" }, chevron = true) { open(Page.PHONE) }
        section("Security")
        row("Regenerate Code", "Changes the secret in the address: phones with the old address can no longer use it and must scan again. " +
            "Do this if a phone that opened the page is lost.", { if (tokenRenewed) "New code created" else "" }) {
            PhoneServer.regenerate(this)
            tokenRenewed = true
        }
        row("Phone Page", "The code in the address is the only protection: anyone who knows it on the same network can control the TV. " +
            "When off, the page does not respond at all.", { onOff(prefs.phoneServer) }) {
            prefs.phoneServer = !prefs.phoneServer
            KeeperService.start(this)
        }
    }

    /** Système: debugging, the home guard, the TV's own settings, updates, the version. */
    private fun systemGroup() {
        section("Debugging")
        row("Local Debugging", "Lets Home shut down the TV, free memory and more through local debugging. In the dialog, select “Always allow”.",
            { if (prefs.adbAuthorized) "Allowed" else "Not allowed, press OK" }) { v ->
            v.text = "Select Always allow…"
            Thread {
                val ok = LocalAdb.shell(applicationContext, "echo ok", 60_000) != null
                runOnUiThread { v.text = if (ok) "Allowed" else "Not allowed, OK to retry" }
            }.start()
        }
        row("ADB Debugging", "Must be on for a computer to connect to the TV: without it, network installation and updates are unavailable. " +
            "Change it in the TV's developer options.",
            { onOff(Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1) }) {}
        if (Guard.isXiaomi() || prefs.keepAdb) row("Keep ADB Debugging On", "Prevents the system from disabling ADB debugging.", { onOff(prefs.keepAdb) }) { v ->
            val on = !prefs.keepAdb
            v.text = "Applying…"
            Thread {
                val ok = !on || Guard.hasSecureSettings(this) || Guard.grantViaLocalAdb(applicationContext)
                if (ok) { prefs.keepAdb = on; Guard.ensureAdb(applicationContext) }
                runOnUiThread { v.text = if (ok) onOff(prefs.keepAdb) else "Local debugging required" }
            }.start()
        }
        row("IP Address", "The TV's local network address. A computer on the same network uses it to connect (adb connect, port 5555), " +
            "for example to install or update canvasOS.", { Net.lanAddress(this) ?: "Not connected" }) {}
        // Only where the TV keeps Home for its own launcher; on Sony or stock Android TV canvasOS is the real home.
        if (Guard.needed(this)) {
            section("Protection")
            row("Home Protection", "When Home opens the TV's original launcher, automatically returns here.",
                { if (!prefs.guard) "Off" else if (HomeGuardService.isRunning) "On" else "On (inactive)" }) { v ->
                val on = !prefs.guard
                v.text = "Applying…"
                Thread {
                    val ok = (Guard.hasSecureSettings(this) || Guard.grantViaLocalAdb(applicationContext)) && Guard.setEnabled(applicationContext, on)
                    runOnUiThread {
                        v.text = if (!ok) "Local debugging required" else if (on) "On" else "Off"
                        if (ok && on) Toast.makeText(this, "Home will now return here instead of the original launcher", Toast.LENGTH_LONG).show()
                    }
                }.start()
            }
        }
        section("TV")
        row("TV Settings", "Opens the TV's original settings.", { "" }, chevron = true) { runCatching { startActivity(TvControls(this).settingsIntent()) } }
        section("Update")
        row("Update", "Checks GitHub Releases for the latest canvasOS version and installs it in place: settings and permissions are kept. " +
            "Press once to check, again to install. Installation requires local debugging. " +
            "canvasOS also checks daily for a version without downloading anything.", { updateValue() }) { v -> updatePressed(v) }
        section("About")
        row("About", "canvasOS, an Android TV home screen inspired by tvOS.",
            { "Version " + packageManager.getPackageInfo(packageName, 0).versionName }) {}
    }

    /** The newer Release the last check in this screen found (what a second press installs), or null. */
    private var foundUpdate: Updater.Update? = null

    /** What the "Mise à jour" row says after a check or a failure ("À jour (1.1.0)", "Releases inaccessibles"); null: what the saved state says. */
    private var updateNote: String? = null

    private fun updateValue(): String = Updater.working ?: updateNote
        ?: Updater.pending(prefs)?.let { "$it available, OK to install" } ?: "Check"

    /**
     * The "Mise à jour" row. Pressed once it asks GitHub; when that finds a newer version it says
     * so, and the next press (or this one, if the row already said so) downloads and installs it.
     * Installing replaces canvasOS, so the screen usually vanishes with the process.
     */
    private fun updatePressed(v: TextView) {
        if (v.text.endsWith("…")) return
        // What the row said when it was pressed: a version already on offer is installed without asking again.
        val offered = foundUpdate != null || (updateNote == null && Updater.pending(prefs) != null)
        fun say(text: String, detail: String? = null) = runOnUiThread {
            v.text = text
            if (detail != null) about.text = detail
        }
        v.text = "Checking…"
        Thread {
            val update = foundUpdate ?: when (val c = Updater.check(applicationContext)) {
                is Updater.Check.UpToDate -> {
                    updateNote = "Up to date (${c.version})"
                    return@Thread say(updateNote!!, "canvasOS ${c.version} is the latest published version.")
                }
                is Updater.Check.Unavailable -> {
                    updateNote = "Releases unavailable"
                    return@Thread say(updateNote!!, c.reason)
                }
                is Updater.Check.Available -> c.update
            }
            foundUpdate = update
            updateNote = null
            val mb = if (update.size > 0) " (${(update.size + 524_288) / 1_048_576} MB)" else ""
            val notes = if (update.notes.isEmpty()) "" else "\n\n" + update.notes.take(400)
            if (!offered) return@Thread say(updateValue(), "canvasOS ${update.version}$mb is available. Press OK to download and install it.$notes")
            if (!prefs.adbAuthorized) {
                say("Select Always allow…")
                if (LocalAdb.shell(applicationContext, "echo ok", 60_000) == null) {
                    updateNote = "Local debugging required"
                    return@Thread say(updateNote!!, "Installation uses local debugging: allow it using “Local Debugging” above, then retry.")
                }
            }
            Updater.downloadAndInstall(applicationContext, update) { say(it) }
                .onSuccess {
                    // Normally the process is gone by now.
                    updateNote = "Installed, restarting app…"
                    say(updateNote!!)
                }
                .onFailure {
                    updateNote = "Failed, OK to retry"
                    say(updateNote!!, it.message)
                }
        }.start()
    }

    private fun appPicker(title: String, selected: Set<String>, includeHidden: Boolean = false, source: List<AppEntry>? = null, toggle: (String, Boolean) -> Unit) {
        heading.text = title
        about.text = if (page == Page.KEEP) "Memory cleanup does not close selected apps." else "Selected apps do not appear on Home."
        val apps = source ?: if (includeHidden) repo.allApps().sortedBy { it.label } else repo.allApps().filter { !repo.isSystem(it.pkg) }.sortedBy { it.label }
        val sel = selected.toMutableSet()
        for (a in apps) {
            row(a.label, null, { if (a.pkg in sel) "✓" else "" }) { v ->
                val on = a.pkg !in sel
                if (on) sel += a.pkg else sel -= a.pkg
                toggle(a.pkg, on)
                v.text = if (on) "✓" else ""
            }
        }
    }

    /**
     * The apps whose notifications never become banners (ticked = excluded): every app on the TV,
     * system ones included (a firmware app may be chatty), and any package excluded over adb.
     */
    private fun bannerAppsPage() {
        val listed = repo.allApps().filter { !repo.isOwn(it.pkg) }
        val known = listed.mapTo(HashSet()) { it.pkg }
        val more = (prefs.bannerExcluded - known).mapNotNull { pkg ->
            runCatching {
                AppEntry(pkg, packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, 0)).toString(), android.content.ComponentName(pkg, ""), false)
            }.getOrNull()
        }
        appPicker("Apps Excluded from Banners", prefs.bannerExcluded, source = (listed + more).sortedBy { it.label.lowercase() }) { pkg, on ->
            prefs.bannerExcluded = if (on) prefs.bannerExcluded + pkg else prefs.bannerExcluded - pkg
        }
        about.text = "Notifications from selected apps are neither read nor displayed. Other apps' notifications appear at the top right for 6 seconds."
    }

    /** Approves the notification listener through local adb, as AGENTS.md step 4 does from a computer. Blocking. */
    private fun allowListener(): Boolean {
        if (!prefs.adbAuthorized) return false
        LocalAdb.shell(applicationContext, "cmd notification allow_listener $packageName/$packageName.RevivalListener; echo done", 10_000) ?: return false
        return Banners.listenerGranted(applicationContext)
    }

    /**
     * The home grid's folders with what they show, read-only: they are made and changed on the home
     * screen itself (long press), where the apps are.
     */
    private fun foldersPage() {
        heading.text = "Folders"
        about.text = "Home folders, in order, with the number of visible apps."
        // What the grid shows: installed apps that are not hidden.
        val folders = repo.orderedVisible().filter { it.folder != null }
        if (folders.isEmpty()) row("No folders", null, { "" }) {}
        for (f in folders) {
            val apps = f.folder.orEmpty()
            row(f.label, if (apps.isEmpty()) "This folder is empty." else apps.joinToString(", ") { it.label },
                { if (apps.size < 2) "${apps.size} app" else "${apps.size} apps" }) {}
        }
        row("Manage folders on Home (long press)", "On Home, hold an app: “New Folder…” " +
            "puts it in a new folder, “Add to Folder…” in an existing folder. Hold a folder to " +
            "rename it, add apps, move or delete it; its apps then return to Home.", { "" }) {}
    }

    /**
     * The profiles: one row each (pressing it renames, recolours, makes it a child's or deletes it),
     * then a new one and the PIN. Every change is saved at once; the page is drawn again after.
     */
    private fun profilesPage() {
        heading.text = "Profiles"
        about.text = PROFILES_ABOUT
        val current = prefs.currentProfile
        for (p in prefs.profileList()) {
            val desc = listOfNotNull(
                if (p.id == current) "The profile currently displayed." else null,
                if (p.restricted) "Child profile: a PIN is required to leave, open settings or uninstall an app; “Hide” and “App Info” are unavailable." else null,
                if (p.isMain) "The main profile: the original Home before profiles. It cannot be deleted." else null,
            ).joinToString("\n").ifEmpty { "Press to rename, change color, make it a child profile or delete it." }
            row(Profiles.displayName(p), desc, { if (p.restricted) "Child" else "" }, picture = ProfileAvatar.drawable(this, p, ROW_AVATAR_DP)) { profileMenu(p) }
        }
        row("New Profile…", "Another profile, with its own Home. Starts with default app order, no hidden apps or folders, " +
            "and the main profile's wallpaper.", { "" }) {
            showSheet(Sheet(root).title("New Profile").message("The profile name, for example “Kids”.")
                .input("", Profiles.DEFAULT_NAME) { name -> prefs.addProfile(name) })
        }
        row("PIN", "Required to leave a child profile, open settings or uninstall an app. ${Profiles.PIN_MIN} to ${Profiles.PIN_MAX} digits; " +
            "changing or deleting it requires the current PIN. If forgotten, remove it from a computer (adb).",
            { if (prefs.hasPin) "Set" else "None" }) { pinMenu() }
    }

    private fun profileMenu(p: Profile) {
        val name = Profiles.displayName(p)
        val s = Sheet(root).title(name)
            .message(if (prefs.hasPin) null else "A child profile is protected only after a PIN is set (“PIN” on this page).")
            .button("Rename") {
                showSheet(Sheet(root).title("Rename “$name”").message(if (p.isMain) "Leave blank to restore the name “${Profiles.MAIN_NAME}”." else null)
                    .input(p.name, if (p.isMain) Profiles.MAIN_NAME else Profiles.DEFAULT_NAME) { text -> prefs.editProfiles { Profiles.rename(it, p.id, text) } })
            }
            .cycle("Color", Profiles.COLOR_NAMES, p.color) { c -> prefs.editProfiles { Profiles.setColor(it, p.id, c) } }
            .button("Photo…") { photoHelp(p) }
        if (Profiles.photoFile(this, p.id) != null) s.button("Remove Photo") {
            Profiles.clearPhoto(this, p.id)
        }
        s.toggle("Child Profile", p.restricted) { on -> prefs.editProfiles { Profiles.setRestricted(it, p.id, on) } }
        if (Profiles.canDelete(prefs.profileList(), p.id, prefs.currentProfile)) s.button("Delete") {
            showSheet(Sheet(root).title("Delete “$name”?").message("Its Home (app order, hidden apps, folders, names, wallpaper) is deleted. The apps stay installed.")
                .button("Delete") { prefs.deleteProfile(p.id) }
                .button("Cancel") {})
        }
        showSheet(s.button("Cancel") {})
    }

    /**
     * "Photo…": a picture cannot be chosen with a remote, so this says where it comes from: the phone
     * page, or a computer (the path carries the profile's id).
     */
    private fun photoHelp(p: Profile) {
        val name = Profiles.displayName(p)
        showSheet(Sheet(root).title("Photo for “$name”")
            .message("Without a photo, the profile shows its initial on its color.\n\n" +
                "From a phone: open the phone page (Home Settings → Phone Page), Settings tab, Profiles section, then “Change Photo”.\n\n" +
                "From a computer: adb push photo.jpg /sdcard/Android/data/$packageName/files/${Profiles.AVATARS_DIR}/${p.id}.jpg")
            .button("OK") {})
    }

    /** "Code PIN": set one, or change or remove it after giving the current one. */
    private fun pinMenu() {
        if (!prefs.hasPin) { pins.choose("Set PIN"); return }
        showSheet(Sheet(root).title("PIN").message(null)
            .button("Edit") { pins.ask("Current PIN") { pins.choose("New PIN") } }
            .button("Delete") {
                pins.ask("Current PIN") {
                    showSheet(Sheet(root).title("Delete PIN?").message("Child profiles will no longer be protected: anyone can leave them and open settings without a PIN.")
                        .button("Delete") { prefs.setPin(null) }
                        .button("Cancel") {})
                }
            }
            .button("Cancel") {})
    }

    /** Preinstalled apps deleted from the long-press menu; selecting one reinstalls it from the system partition. */
    private fun removedApps() {
        heading.text = "Removed Preinstalled Apps"
        about.text = "Choose an app to reinstall from the system partition."
        if (prefs.removed.isEmpty()) row("No removed apps", null, { "" }) {}
        for (pkg in prefs.removed.sorted()) {
            val label = try {
                packageManager.getApplicationInfo(pkg, PackageManager.MATCH_UNINSTALLED_PACKAGES).loadLabel(packageManager).toString()
            } catch (e: Exception) { pkg }
            row(label, null, { "Restore" }) { v ->
                v.text = "Restoring…"
                Thread {
                    val ok = LocalAdb.shell(applicationContext, "cmd package install-existing $pkg")?.contains("installed") == true
                    if (ok) prefs.removed = prefs.removed - pkg
                    runOnUiThread { v.text = if (ok) "Restored" else "Failed" }
                }.start()
            }
        }
    }

    /** The phone page's QR code, large enough to scan from the sofa, with its address. */
    private fun phonePage() {
        heading.text = "Phone Page"
        about.text = "On the same Wi-Fi, scan the code with a phone's camera; the page remains accessible anytime. " +
            "Remote and keyboard, photos and image links, startup animation, app installation (APKs).\n" +
            "The code in the address is the only protection: regenerate it if a phone that opened the page is lost; " +
            "phones with the old address will need to scan again."
        val qr = ImageView(this).apply {
            background = rounded(Color.WHITE, u(ROW_RADIUS).toFloat())
            setPadding(u(12), u(12), u(12), u(12))
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val address = label(15f, DESCRIPTION).apply { gravity = Gravity.CENTER_HORIZONTAL }
        // The same page by name (Mdns): the QR code stays on the IP, which every phone resolves.
        val byName = label(13f, DESCRIPTION).apply { gravity = Gravity.CENTER_HORIZONTAL }
        list.addView(qr, LinearLayout.LayoutParams(u(210), u(210)).apply { gravity = Gravity.CENTER_HORIZONTAL })
        list.addView(address, LinearLayout.LayoutParams(-1, -2).apply { topMargin = u(12) })
        list.addView(byName, LinearLayout.LayoutParams(-1, -2).apply { topMargin = u(4); bottomMargin = u(16) })
        fun load() {
            address.text = "Starting…"
            byName.visibility = android.view.View.GONE
            Thread {
                // Usually already running (KeeperService); the address is read again, Wi-Fi may have changed.
                PhoneServer.start(applicationContext)
                val url = PhoneServer.url(applicationContext)
                val host = url?.let { PhoneServer.hostUrl(applicationContext) }
                val bmp = url?.let { PhoneServer.qr(it) }
                val off = if (url == null) PhoneServer.unavailable(applicationContext) else null
                runOnUiThread {
                    qr.setImageBitmap(bmp)
                    qr.visibility = if (bmp != null) android.view.View.VISIBLE else android.view.View.GONE
                    address.text = url ?: off
                    byName.text = host?.let { "Also: $it (recent iPhones and Android phones)" }
                    byName.visibility = if (host != null) android.view.View.VISIBLE else android.view.View.GONE
                }
            }.start()
        }
        load()
        // No description of its own: the page's explanation stays under the icon.
        row("Regenerate Code", null, { "" }) { v ->
            PhoneServer.regenerate(this)
            tokenRenewed = true
            v.text = "New code created"
            load()
        }
    }

    /** When the last memory cleanup ran ("22 oct. 21:40"), from [Prefs.lastClean] ("22 oct. 21:40, 312 Mo libérés, 4 apps fermées"). */
    private fun lastCleanWhen() = prefs.lastClean.substringBefore(", ").ifEmpty { "Never" }

    /** What it did: "312 Mo libérés, 4 apps fermées". */
    private fun lastCleanWhat() = prefs.lastClean.substringAfter(", ", "")

    /** The apps the last memory cleanup closed, by name. Nothing here does anything; Back returns. */
    private fun lastCleanPage() {
        heading.text = "Last Cleanup"
        val cacheOnly = lastCleanWhat().endsWith("(cache only)")
        about.text = when {
            prefs.lastClean.isEmpty() -> "Memory has not been freed yet."
            cacheOnly -> "${lastCleanWhen()} : ${lastCleanWhat()}.\nWithout local debugging, only cached apps were stopped, and Android does not report which ones."
            else -> "${lastCleanWhen()} : ${lastCleanWhat()}.\nApps to keep, and those playing audio or video or running " +
                "a service (casting, server), were not closed."
        }
        val labels = prefs.lastCleanClosed.split(',').filter { it.isNotBlank() }.map { pkg ->
            runCatching { packageManager.getApplicationInfo(pkg, 0).loadLabel(packageManager).toString() }.getOrDefault(pkg)
        }.sortedBy { it.lowercase() }
        if (labels.isEmpty()) row(if (cacheOnly) "Details unavailable" else "No apps closed", null, { "" }) {}
        for (label in labels) row(label, null, { "" }) {}
    }

    /** A small grey uppercase title above a run of rows, as tvOS splits a settings page ("AUTO-PLAY", "HOME SCREEN"). */
    private fun section(title: String) {
        val first = list.childCount == 0
        list.addView(label(HEADER_SIZE, HEADER, 600).apply {
            text = title.uppercase(java.util.Locale.ENGLISH)
            letterSpacing = 0.02f
            tag = HEADER_TAG
        }, LinearLayout.LayoutParams(-2, -2).apply {
            // Lined up with the rows' labels; the row above already keeps its gap below it.
            marginStart = u(ROW_PAD); topMargin = if (first) 0 else u(HEADER_ABOVE); bottomMargin = u(HEADER_BELOW)
        })
    }

    /**
     * One settings row; [value] is re-read after each click, [desc] shows under the icon while
     * the row is focused, and [chevron] marks a row that opens another screen.
     */
    private fun row(label: String, desc: String?, value: () -> String, chevron: Boolean = false, picture: android.graphics.drawable.Drawable? = null, onClick: (TextView) -> Unit) {
        val r = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isFocusable = true
            isClickable = true
            setPadding(u(ROW_PAD), 0, u(if (chevron) ROW_PAD + 2 else ROW_PAD), 0)
        }
        val l = label(18.5f, IDLE_TEXT, 500).apply {
            text = label
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        val v = label(18.5f, IDLE_VALUE).apply {
            text = value()
            maxLines = 1
        }
        picture?.let { d -> r.addView(ImageView(this).apply { setImageDrawable(d) }, LinearLayout.LayoutParams(u(ROW_AVATAR), u(ROW_AVATAR)).apply { marginEnd = u(10) }) }
        r.addView(l, LinearLayout.LayoutParams(0, -2, 1f))
        r.addView(v, LinearLayout.LayoutParams(-2, -2).apply { marginStart = u(12) })
        val arrow = if (chevron) Chevron(IDLE_VALUE, maxOf(1.6f * unit, 1.5f)) else null
        arrow?.let { r.addView(android.view.View(this).apply { background = it }, LinearLayout.LayoutParams(u(8), u(15)).apply { marginStart = u(12) }) }
        val idle = rounded(IDLE_ROW, u(ROW_RADIUS).toFloat())
        val focus = rounded(Color.WHITE, u(ROW_RADIUS).toFloat())
        r.background = idle
        r.setOnFocusChangeListener { _, f ->
            r.background = if (f) focus else idle
            l.setTextColor(if (f) Color.BLACK else IDLE_TEXT)
            v.setTextColor(if (f) FOCUS_VALUE else IDLE_VALUE)
            arrow?.color = if (f) FOCUS_VALUE else IDLE_VALUE
            if (f && desc != null) about.text = desc
            // A slight lift with a soft shadow under it, as the focused row on tvOS.
            r.tweenScale(if (f) 1.02f else 1f, 160, z = if (f) u(6).toFloat() else 0f)
        }
        r.setOnClickListener {
            val from = page
            onClick(v)
            // On a group page the value shows what the press changed; list pages set their own.
            if (page == from && from.title != null && !v.text.endsWith("…")) v.text = value()
        }
        list.addView(r, LinearLayout.LayoutParams(-1, u(ROW_H)).apply { bottomMargin = u(ROW_GAP) })
    }

    /** tvOS's thin chevron at the end of a row that opens a page: two strokes with round ends. */
    private class Chevron(color: Int, stroke: Float) : android.graphics.drawable.Drawable() {
        private val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = stroke
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            this.color = color
        }
        var color: Int
            get() = paint.color
            set(v) { paint.color = v; invalidateSelf() }

        override fun draw(c: android.graphics.Canvas) {
            val b = bounds
            val i = paint.strokeWidth / 2
            c.drawPath(android.graphics.Path().apply {
                moveTo(b.left + i, b.top + i)
                lineTo(b.right - i, b.exactCenterY())
                lineTo(b.left + i, b.bottom - i)
            }, paint)
        }
        override fun setAlpha(alpha: Int) { paint.alpha = alpha }
        override fun setColorFilter(cf: android.graphics.ColorFilter?) { paint.colorFilter = cf }
        @Suppress("OVERRIDE_DEPRECATION")
        override fun getOpacity() = android.graphics.PixelFormat.TRANSLUCENT
    }

    private fun onOff(b: Boolean) = if (b) "On" else "Off"

    /** "自动（…）" with what auto picks on this TV, or the forced choice. */
    private fun auto(pref: String, on: Boolean, yes: String, no: String) =
        if (pref == Prefs.AUTO) "Auto (${if (on) yes else no})" else if (on) yes else no

    /** The next of 自动 → [a] → [b]. */
    private fun next(pref: String, a: String, b: String) = when (pref) { Prefs.AUTO -> a; a -> b; else -> Prefs.AUTO }

    /** A secure setting, written directly once WRITE_SECURE_SETTINGS is granted, otherwise through local adb. Blocking. */
    private fun putSecure(key: String, value: String): Boolean =
        if (Guard.hasSecureSettings(this)) runCatching { Settings.Secure.putString(contentResolver, key, value) }.getOrDefault(false)
        else LocalAdb.shell(applicationContext, "settings put secure $key $value") != null

    private fun minutesLabel(m: Int) = when {
        m <= 0 -> "Never"
        m >= 1440 -> "Daily"
        m >= 60 -> "${m / 60} h"
        else -> "$m min"
    }

    /** On Android TV the screensaver starts after the "screen off" timeout. */
    private fun idleMinutes() = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 600_000) / 60_000

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && sheets.isNotEmpty()) {
            sheets.last().dismiss()
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && page != Page.MAIN) {
            // Up one level, onto the row that opened this page; the page the screen was opened on leaves.
            val up = page.parent
            if (page == entry || up == null) finish() else show(up, openers[page] ?: 0)
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
        if (keyCode == KeyEvent.KEYCODE_BACK && (page != Page.MAIN || sheets.isNotEmpty())) true else super.onKeyDown(keyCode, event)
}
