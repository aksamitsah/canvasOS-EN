import type { Copy } from "./lang";
import { brand, cmds } from "./site";
import type { Cmd } from "./site";

/* Local alias keeps the copy readable. */
const C: typeof cmds = cmds;

export const zh = {
    skip: "Aller au contenu principal",
    navLabel: "Navigation principale",
    menuLabel: "Ouvrir le menu",
    menuCloseLabel: "Fermer le menu",
    nav: [
      { href: "#features", label: "Fonctionnalités" },
      { href: "#aerial", label: "Vues aériennes" },
      { href: "#compare", label: "Avant / après" },
      { href: "#install", label: "Installation" },
      { href: "#compatibility", label: "Compatibilité" },
      { href: "#faq", label: "FAQ" },
    ],
    langSwitch: { href: "/en/", label: "EN", lang: "en" },
    headerCta: "Télécharger",
    menuCta: "Télécharger l'APK canvasOS",
    menuNote: "Android TV 9+ · sans root · projet open source indépendant, sans lien avec Apple",
    copy: "Copier",
    copied: "Copié",
    copyFailed: "Échec de la copie",
    compareLabel: "Comparer l'illustration d'un lanceur générique avec une capture réelle de canvasOS",
    compareValue: (p: number) => `${p} % de l'image « avant » affichés`,
    slideshow: { prev: "Précédente", next: "Suivante" },
    lightbox: { close: "Fermer", prev: "Précédente", next: "Suivante", name: "Visionneuse de captures" },
    year: 2026,

    meta: {
      title: "canvasOS — l'accueil de votre TV, de nouveau éblouissant | Lanceur Android TV",
      description:
        "canvasOS est un lanceur Android TV open source : Dock en verre liquide, contenus Top Shelf, Centre de contrôle en verre dépoli, économiseur d'écran avec les vues aériennes de l'Apple TV et fonds d'écran de paysages. Android 9+, sans root, vérifié sur Sony BRAVIA.",
      canonical: `${brand.site}/`,
      alts: [
        { hreflang: "fr", href: `${brand.site}/` },
        { hreflang: "en", href: `${brand.site}/en/` },
        { hreflang: "x-default", href: `${brand.site}/` },
      ],
      ogLocale: "fr_FR",
      ogLocaleAlt: "en_US",
      ogTitle: "canvasOS — l'accueil de votre TV, de nouveau éblouissant",
      ogDescription:
        "Lanceur Android TV open source : Dock en verre liquide, Top Shelf, Centre de contrôle, économiseur d'écran aérien façon Apple TV. Android 9+, sans root.",
    },

    hero: {
      eyebrow: "CANVASOS · ANDROID TV LAUNCHER",
      titleLines: ["L'accueil de votre TV,", "de nouveau éblouissant."],
      sub: "Transformez votre Android TV en une porte d'entrée immersive digne de tvOS. Un Dock en verre liquide posé en bas de l'écran, des contenus qui respirent en haut, et les vues aériennes de l'Apple TV en économiseur d'écran — canvasOS rend à l'écran du salon l'allure qu'il mérite.",
      primary: "Télécharger gratuitement",
      secondary: "Voir l'installation",
      chips: ["Android TV 9+", "Sans root", "Certaines fonctions varient selon la marque"],
      note: "Projet open source indépendant, sans aucun lien avec Apple. « Apple TV » est une marque d'Apple Inc.",
      imageAlt:
        "Capture réelle de l'accueil canvasOS : sur un fond de paysage, un Dock en verre liquide aligne les icônes d'applications 云视听小电视, 云视听极光, Mango TV et Douyu.",
      floating: [
        { title: "Liquid Glass", sub: "Dock en verre posé en bas" },
        { title: "Vues aériennes", sub: "Les films aériens de l'Apple TV" },
      ],
    },

    proof: [
      "Vérifié sur Sony BRAVIA",
      "Économiseur d'écran aérien Apple TV",
      "Deux APK : standard et protection",
      "Sans root, réversible à tout moment",
    ],

    compare: {
      eyebrow: "BEFORE / AFTER",
      title: "Faites glisser, voyez la différence.",
      lede: "À gauche, une <strong>illustration (pas une capture du même appareil)</strong> d'un accueil en mosaïque classique avant installation ; à droite, une <strong>vraie capture</strong> de canvasOS sur un Sony BRAVIA. Faites glisser le curseur central pour comparer.",
      beforeLabel: "Avant",
      beforeSub: "Illustration · pas le même appareil",
      afterLabel: "Après",
      afterSub: "canvasOS · capture réelle",
      beforeAlt: "Illustration d'une disposition d'accueil Android TV générique, pas une capture réelle.",
      afterAlt: "Capture réelle de l'accueil canvasOS : un Dock en verre liquide sur une photo de paysage.",
      hintKeys: "Faites glisser, cliquez, ou placez le focus sur la poignée et utilisez ← →, Maj pour aller plus vite, double-clic pour revenir au centre.",
      disclaimer: "L'image « avant » est une illustration, pas une capture réelle du même appareil.",
    },

    aerial: {
      eyebrow: "AERIAL SCREENSAVER",
      title: "Les vues aériennes de l'Apple TV, en économiseur d'écran.",
      lede: "Quand la TV est inactive, elle survole lentement la Terre : côtes, glaciers et villes défilent, et le nom du lieu change avec chaque plan. Les touches gauche et droite changent de plan, l'économiseur repasse aux photos sans connexion, et il n'interrompt jamais une vidéo en cours.",
      bullets: [
        "Choisissez « Films aériens » comme contenu de l'économiseur d'écran : les images sont lues en ligne depuis les serveurs d'Apple (environ 15–20 Mbit/s)",
        "Sans connexion ou en cas d'échec de mise en mémoire tampon, retour automatique aux photos, sans gêner la prochaine mise en veille",
        "Après 1 à 30 minutes sans utiliser la télécommande, le « Cadre photo » démarre ; l'heure et la légende se déplacent légèrement pour éviter le marquage de l'écran",
      ],
      caption: "Cadre photo · photo du jour Bing (démo : mode photos)",
      frameAlt: "Capture réelle du Cadre photo : une pieuvre sous l'eau, avec l'heure, la date et la légende en bas à gauche.",
      bulletsTitle: "Comment l'activer",
      attribution:
        "Les films aériens sont diffusés en ligne par les serveurs d'Apple ; canvasOS ne contient aucun fichier vidéo. « Apple TV » est une marque d'Apple Inc. Ce projet n'a aucun lien avec Apple.",
    },

    features: {
      eyebrow: "FEATURES",
      title: "Redessiné pour trois mètres de distance.",
      lede: "Pas une interface de téléphone agrandie, mais une mise en page pensée pour le salon : un focus plus grand, moins de niveaux, un verre plus discret.",
    },

    film: {
      eyebrow: "FILM",
      title: "42 secondes pour découvrir canvasOS.",
      lede: "Découvrez comment canvasOS transforme l'accueil de votre TV. Lancez la lecture et montez le son.",
      label: "Lire le film de présentation de canvasOS (avec son)",
    },
    gallery: {
      eyebrow: "SCREENSHOTS",
      title: "Tout vient d'une vraie télé.",
      lede: "Sony BRAVIA XR-55A80J, Android TV 12, captures d'écran et enregistrements réels en 1280 × 720. Cliquez sur une image pour l'agrandir.",
    },

    install: {
      eyebrow: "INSTALL",
      title: "Du téléchargement au nouvel accueil, étape par étape.",
      lede: "Un ordinateur sur le même Wi-Fi que la TV, l'adresse IP de la TV et le débogage ADB activé : c'est tout. Android 9 ou plus, sans root.",
      packages: [
        {
          name: "canvasOS.apk",
          use: "Version standard. Sony BRAVIA, Android TV / Google TV d'origine, Xiaomi / Redmi, Hisense et la plupart des téléviseurs.",
          href: brand.apk,
        },
        {
          name: "canvasOS-guard.apk",
          use: "Version protection. Ne se déclare pas comme écran d'accueil ; pour les firmwares TCL / FFalcon qui refusent les APK de lanceur, installée via « Interaction multi-écran (多屏互动) ».",
          href: brand.apkGuard,
        },
      ],
      prereq:
        "Avant de commencer : l'ordinateur et la TV sont sur le même réseau local ; le débogage ADB est activé sur la TV ; à la première connexion, la TV demande « Autoriser le débogage USB ? » : cochez « Toujours autoriser ». L'ordinateur a besoin des Android SDK Platform-Tools.",
      shellNote:
        "Chaque commande existe en deux versions : choisissez d'abord votre système ci-dessus. Sous Windows PowerShell, le téléchargement passe automatiquement par curl.exe et python3 devient python ; macOS / Linux utilisent curl et python3 fournis par le système. Les commandes n'utilisent pas de variables et le nom du paquet est toujours écrit com.leslie.tvhome : vous pouvez donc en copier une partie et l'exécuter telle quelle.",
      shellLabels: { posix: "macOS / Linux", powershell: "Windows PowerShell" },
      placeholderNote:
        "Dans les exemples, 192.168.x.x est une valeur fictive : remplacez-le par l'IP réelle de la TV. Le PIN de l'étape d'enregistrement BRAVIA est le code à quatre chiffres affiché à l'écran de la TV ; remplacez-le comme indiqué.",
      adbTable: {
        caption: "Activer le débogage ADB selon la marque de la TV",
        head: ["TV", "Comment activer ADB"],
        rows: [
          [
            "Xiaomi / Redmi",
            "Paramètres (设置) → À propos (关于) → <strong>Modèle du produit (产品型号)</strong> : appuyez plusieurs fois sur OK jusqu'à activer le mode développeur ; puis Paramètres → Compte et sécurité (账号与安全) : activez <strong>Débogage ADB (ADB 调试)</strong> et <strong>Installer des applications de sources inconnues (安装未知来源的应用)</strong>.",
          ],
          [
            "TCL / FFalcon",
            "Paramètres (设置) → À propos (关于) → <strong>Informations sur l'appareil (本机信息)</strong>, puis appuyez sur <strong>haut, bas, gauche, droite</strong> sur la télécommande (moins d'une seconde d'intervalle) ; un groupe « Plus (更多) » apparaît en bas : activez-y <strong>ADB</strong>.",
          ],
          [
            "Sony BRAVIA",
            "Paramètres → Système → À propos → appuyez 7 fois sur OK sur <strong>Build</strong> ; puis Paramètres → Système → Options pour les développeurs : activez <strong>Débogage USB</strong>.",
          ],
          [
            "Android TV / Google TV d'origine, Hisense, etc.",
            "Paramètres → Préférences de l'appareil → À propos → appuyez 7 fois sur <strong>Build</strong> ; puis dans Options pour les développeurs, activez <strong>Débogage USB</strong> (parfois appelé « Débogage réseau » ou « Débogage ADB »).",
          ],
        ],
      },
      tabsLabel: "Choisir la marque de la TV",
      tabs: [
        {
          id: "sony",
          label: "Sony BRAVIA",
          intro:
            "Le parcours entièrement vérifié, luminosité, volume de la TV et sources HDMI compris. Ci-dessous, 192.168.x.x désigne l'IP de la TV.",
          steps: [
            {
              title: "Connecter la TV",
              body: "À lancer sur l'ordinateur ; quand la TV affiche la demande d'autorisation, choisissez « Autoriser » et cochez « Toujours autoriser ».",
              cmd: C.connect,
            },
            {
              title: "Noter le lanceur, l'économiseur d'écran et les services d'accessibilité d'origine",
              body: "Il faudra les restaurer à la désinstallation : gardez-les dans un fichier texte.",
              cmd: C.snapshotBeforeInstall,
            },
            {
              title: "Télécharger et installer la version standard",
              cmd: C.installStandard,
              note: "Sous Windows PowerShell, utilisez `curl.exe -L -o canvasOS.apk …`.",
            },
            {
              title: "Définir comme accueil par défaut et accorder les autorisations",
              body: "Les trois autorisations servent respectivement à : la protection et l'économiseur d'écran, la relance après un arrêt forcé, l'ouverture de l'accueil en arrière-plan au démarrage.",
              cmd: C.setHomeAndGrant,
            },
            {
              title: "Facultatif : enregistrer le contrôle IP BRAVIA",
              body: "Nécessaire une fois pour la luminosité, le volume de la TV et les sources HDMI. Après la première commande, la TV affiche un PIN à 4 chiffres : utilisez-le dans la seconde.",
              cmd: C.braviaRegister,
              note: "Cette étape utilise une variable shell : elle ne fonctionne que dans un terminal macOS / Linux.",
            },
            {
              title: "Autoriser le débogage local",
              body: "Au premier lancement, canvasOS demande l'autorisation de débogage local, utilisée pour la veille, l'extinction, le redémarrage et le nettoyage complet des applications en arrière-plan. Cochez « Toujours autoriser » sur la TV, puis ouvrez le Centre de contrôle et vérifiez que la ligne « Débogage local non autorisé » a disparu.",
            },
            {
              title: "Vérifier par une capture d'écran",
              cmd: C.verify,
              note: "Vous devriez voir une horloge compacte en haut à droite, deux boutons ronds et le Dock en verre en bas.",
            },
          ],
        },
        {
          id: "tcl",
          label: "TCL / FFalcon",
          intro:
            "Le firmware chinois refuse les adb install classiques ainsi que les APK qui se déclarent comme écran d'accueil. Utilisez la version guard et installez-la via le service « Interaction multi-écran (多屏互动) » intégré à la TV.",
          danger:
            "Certains firmwares TCL / FFalcon n'ont pas d'interface de rétroéclairage utilisable : le curseur de luminosité est alors masqué, et la ligne Sources ouvre le sélecteur de sources de TCL. C'est une limite du système, pas un échec d'installation.",
          steps: [
            { title: "Connecter la TV", cmd: C.connect },
            {
              title: "Noter le lanceur, l'économiseur d'écran et les services d'accessibilité d'origine",
              body: "Ils serviront à tout restaurer à la désinstallation.",
              cmd: C.snapshotBeforeInstall,
            },
            {
              title: "Envoyer la version guard via l'Interaction multi-écran",
              body: "Le script sert l'APK sur le port local 8765 et se connecte au port 6553 de la TV. Quand « 多屏互动：正在发起应用推送 » (envoi d'application en cours) apparaît dans un coin de la TV, choisissez <strong>同意</strong> (Accepter) avec la télécommande.",
              cmd: C.tclPush,
              note: "Windows : remplacez `curl` par `curl.exe` et `python3` par `python` ; si le pare-feu le demande, autorisez le port 8765.",
            },
            {
              title: "Vérifier l'installation",
              cmd: C.packagePath,
            },
            {
              title: "Accorder les autorisations et activer la Protection de l'accueil",
              body: "La version guard ne déclare pas d'écran d'accueil : c'est la « Protection de l'accueil » qui la garde au premier plan. TCL bloque par défaut le démarrage automatique des applications tierces, il faut donc autoriser les deux appops.",
              cmd: C.tclGrants,
            },
            {
              title: "Vérifier que la protection fonctionne",
              cmd: C.tclCheck,
              note: "La sortie doit contenir `com.leslie.tvhome/com.leslie.tvhome.HomeGuardService` et la dernière ligne doit être `com.leslie.tvhome/.HomeActivity`.",
            },
          ],
        },
        {
          id: "xiaomi",
          label: "Xiaomi / Redmi",
          intro:
            "Xiaomi accepte set-home-activity, mais la touche Accueil peut encore ramener à PatchWall. La bonne méthode : garder le lanceur d'origine et laisser la « Protection de l'accueil » ramener canvasOS au premier plan.",
          danger:
            "<strong>Ne désactivez jamais le lanceur intégré de Xiaomi</strong> (com.mitv.tvhome). La TV démarrerait sur un écran noir, récupérable uniquement via ADB.",
          steps: [
            { title: "Connecter la TV", cmd: C.connect },
            {
              title: "Noter le lanceur, l'économiseur d'écran et les services d'accessibilité d'origine",
              cmd: C.snapshotBeforeInstall,
            },
            {
              title: "Installer la version standard",
              cmd: C.installStandard,
              note: "Sous Windows PowerShell, utilisez `curl.exe`.",
            },
            {
              title: "Définir comme accueil par défaut et accorder les autorisations",
              cmd: C.xiaomiSetup,
            },
            {
              title: "Activer la Protection de l'accueil et garder ADB activé",
              body: "keep_adb true permet à canvasOS de réactiver ADB après chaque réveil d'une TV Xiaomi, pour faciliter les futures mises à jour ; omettez ce paramètre si vous n'en avez pas besoin.",
              cmd: C.xiaomiGuard,
            },
            {
              title: "Vérifier",
              body: "Appuyez sur Accueil : le lanceur d'origine peut apparaître une demi-seconde, puis canvasOS revient. Si la protection ne démarre pas, activez-la dans canvasOS via Réglages → Protection de l'accueil (après avoir autorisé le débogage local).",
            },
          ],
        },
        {
          id: "stock",
          label: "Android TV d'origine",
          intro:
            "Le parcours standard. Une fois installé, canvasOS devient l'accueil par défaut, sans modifier la partition système ni désinstaller la moindre application.",
          steps: [
            {
              title: "Se connecter et vérifier la version du système",
              cmd: C.connect,
              note: "Lancez ensuite `adb -s 192.168.x.x:5555 shell getprop ro.build.version.sdk` : la valeur doit être au moins 28 (Android 9).",
            },
            {
              title: "Noter le lanceur, l'économiseur d'écran et les services d'accessibilité d'origine",
              cmd: C.snapshotBeforeInstall,
            },
            {
              title: "Installer et définir comme accueil par défaut",
              cmd: C.stockInstall,
              note: "Sous Windows PowerShell, utilisez `curl.exe`.",
            },
            {
              title: "Accorder les autorisations que seul ADB peut donner",
              cmd: `adb -s 192.168.x.x:5555 shell pm grant com.leslie.tvhome android.permission.WRITE_SECURE_SETTINGS
adb -s 192.168.x.x:5555 shell cmd notification allow_listener com.leslie.tvhome/com.leslie.tvhome.RevivalListener
adb -s 192.168.x.x:5555 shell appops set com.leslie.tvhome SYSTEM_ALERT_WINDOW allow`,
            },
            {
              title: "Si la touche Accueil ramène encore au lanceur d'origine",
              body: "Essayez d'abord la Protection de l'accueil. Ne désactivez le lanceur du fabricant qu'en dernier recours, si vous êtes sûr que la TV n'en dépend pas et avec votre accord ; gardez aussi la commande pour l'annuler.",
              cmd: C.disableVendor,
              note: "N'utilisez jamais cette étape sur Xiaomi / Redmi.",
            },
          ],
        },
      ],
      advanced: {
        title: "Avancé : à quoi servent ces autorisations",
        body: "Les trois autorisations accordées par ADB dans les commandes d'installation ne sont pas accessibles aux applications ordinaires. Elles n'agissent que localement sur l'appareil et ne transmettent rien sur le réseau.",
        rows: [
          {
            name: "WRITE_SECURE_SETTINGS",
            use: "Réactiver la protection quand le système la coupe, garder le Cadre photo comme économiseur d'écran système, garder ADB activé après le réveil d'une TV Xiaomi",
          },
          {
            name: "Écoute des notifications (RevivalListener)",
            use: "Relancer canvasOS environ une demi-seconde après un arrêt forcé par le système ou au démarrage ; aucune notification n'est lue",
          },
          {
            name: "SYSTEM_ALERT_WINDOW",
            use: "Ouvrir l'accueil depuis l'arrière-plan au démarrage ; aucune fenêtre flottante publicitaire ni superposition",
          },
          {
            name: "TCL APP_AUTO_START / APP_ASSOC_START",
            use: "TCL uniquement : autorise le démarrage automatique des applications tierces",
          },
        ],
      },
      agent: {
        title: "Laisser une IA l'installer pour vous (recommandé)",
        body: "Pas besoin de maîtriser la ligne de commande : envoyez la phrase ci-dessous avec le lien du dépôt à un agent capable d'exécuter des commandes, comme Codex ou Claude Code. Il confirmera d'abord la marque et l'IP, puis installera et vérifiera en suivant AGENTS.md, en gardant un moyen de revenir à l'accueil d'origine.",
        prompt:
          "Suis https://github.com/getisolated/canvasOS/blob/main/AGENTS.md pour installer canvasOS sur mon Android TV. Confirme d'abord la marque et l'adresse IP de ma TV, puis installe et vérifie étape par étape, en gardant un moyen documenté de restaurer l'accueil d'origine. Ne désactive pas le lanceur système sans mon accord.",
        note: "Pendant l'installation, la TV affiche deux ou trois demandes d'autorisation : choisissez « Autoriser » avec la télécommande. Sur TCL, quand « 多屏互动：正在发起应用推送 » s'affiche, choisissez « 同意 » (Accepter).",
      },
      safety:
        "<strong>N'éteignez et ne redémarrez jamais la TV à distance sans demander.</strong> Une TV éteinte ne peut être rallumée qu'avec sa télécommande. L'installation ne nécessite de désinstaller ni de désactiver aucune application système.",
    },

    compatibility: {
      eyebrow: "COMPATIBILITY",
      title: "Ce que votre TV peut faire.",
      lede: "canvasOS essaie d'abord l'interface propre à la marque, puis se rabat sur l'interface Android standard. Ce tableau décrit les limites réelles de l'implémentation actuelle, pas un argumentaire commercial.",
      caption: "Android TV 9+ · versions standard et protection identiques, seule la déclaration d'écran d'accueil diffère",
      head: ["Fonction", "Toute Android TV", "Avec débogage local autorisé", "Sony BRAVIA uniquement"],
      rows: [
        { feature: "Accueil, Dock, effets de verre, ombres", any: "Oui", debug: "—", sony: "—" },
        { feature: "Contenus du haut (Bilibili / Douyu)", any: "Oui, avec l'app TV correspondante installée", debug: "—", sony: "—" },
        { feature: "Fonds d'écran paysages / anime, Cadre photo", any: "Oui", debug: "—", sony: "—" },
        { feature: "Économiseur d'écran aérien Apple TV", any: "Oui, connexion requise", debug: "—", sony: "—" },
        { feature: "Centre de contrôle, horloge", any: "Oui", debug: "—", sony: "—" },
        { feature: "Libérer la mémoire", any: "Vide le cache", debug: "Ferme complètement les apps en arrière-plan", sony: "—" },
        { feature: "Supprimer les apps préinstallées", any: "—", debug: "Oui", sony: "—" },
        { feature: "Veille, extinction, redémarrage", any: "—", debug: "Oui", sony: "—" },
        { feature: "Cadre photo comme économiseur d'écran système, délai d'inactivité", any: "—", debug: "Oui", sony: "—" },
        { feature: "Curseur de volume", any: "Volume Android standard", debug: "—", sony: "Volume de la TV" },
        { feature: "Changement de source HDMI", any: "La plupart des TV", debug: "—", sony: "Avec affichage de la source actuelle" },
        { feature: "Curseur de luminosité (rétroéclairage)", any: "Xiaomi, avec autorisation ADB", debug: "—", sony: "Oui" },
        { feature: "Protection de l'accueil, relance automatique", any: "Une autorisation ADB depuis l'ordinateur", debug: "Oui", sony: "—" },
      ],
      notes: [
        {
          title: "Qu'est-ce que le débogage local ?",
          body: "canvasOS se connecte au 127.0.0.1:5555 de la TV elle-même pour exécuter des commandes système. La TV affiche « Autoriser le débogage ? » : cochez « Toujours autoriser ». Une seule autorisation, valable pour toutes les marques.",
        },
        {
          title: "Sony BRAVIA",
          body: "Une fois le contrôle IP enregistré, luminosité, volume et sources passent par l'interface de la TV ; la ligne Sources affiche aussi le signal actuel.",
        },
        {
          title: "TCL / FFalcon (firmware chinois)",
          body: "Pas d'enregistrement standard des sources : Sources ouvre le sélecteur de TCL ; pas d'interface de rétroéclairage utilisable : le curseur de luminosité est masqué ; la version guard est nécessaire.",
        },
        {
          title: "Xiaomi / Redmi (firmware chinois)",
          body: "Le rétroéclairage passe par picture_backlight, les sources par le lecteur de Xiaomi ; ne désactivez pas le lanceur intégré, utilisez la Protection de l'accueil.",
        },
        {
          title: "Coocaa / Skyworth",
          body: "Sans sources standard, Sources ouvre le sélecteur de sources du lanceur Coocaa ; la luminosité et les réglages passent toujours par l'interface Android standard. Pas encore testé sur une vraie TV.",
        },
        {
          title: "Autres marques",
          body: "Le volume passe par AudioManager, les sources par TvInputManager. Android n'a pas d'interface standard pour le rétroéclairage : le curseur de luminosité est masqué. Chaque marque correspond à un fichier d'adaptateur dans le dossier tv/ : les contributions pour votre TV sont les bienvenues.",
        },
        {
          title: "Amplificateur / ARC · eARC",
          body: "Le volume standard peut contrôler la TV elle-même plutôt que l'amplificateur ; le comportement du volume CEC dépend des firmwares de la TV et de l'amplificateur.",
        },
      ],
      scope:
        "<strong>Périmètre vérifié :</strong> la vérification complète n'a été faite que sur un Sony BRAVIA XR-55A80J (Android TV 12). Les adaptations TCL / FFalcon et Xiaomi proviennent de notes de la communauté sur de vrais appareils et n'ont pas encore été vérifiées une à une par le projet.",
    },

    faq: {
      eyebrow: "FAQ",
      title: "Les questions que vous vous posez sûrement.",
      items: [
        {
          q: "Faut-il le root ? Cela affecte-t-il la garantie ?",
          a: [
            "Pas besoin de root, et la partition système n'est pas modifiée. La version standard se définit simplement comme accueil par défaut via ADB et reçoit quelques autorisations inaccessibles aux applications ordinaires.",
            "L'effet sur la garantie dépend des conditions du fabricant de la TV ; le projet ne peut rien garantir. Le vrai risque vient d'une mauvaise commande ADB, par exemple désactiver le lanceur intégré de Xiaomi, ce qui donne un écran noir.",
          ],
        },
        {
          q: "Comment revenir à l'accueil d'origine ?",
          a: [
            "Une fois canvasOS désinstallé, la TV revient à son lanceur d'origine. Désactivez d'abord la protection et restaurez l'économiseur d'écran d'origine, puis désinstallez ; dans les commandes ci-dessous, remplacez les chevrons par les valeurs notées avant l'installation.",
          ],
          cmds: [
            { text: cmds.uninstall },
            { text: cmds.restoreScreensaver },
            { text: cmds.uninstallPackage },
            { text: cmds.reenableVendor },
          ],
        },
        {
          q: "Pourquoi ne pas désactiver le lanceur intégré sur Xiaomi / Redmi ?",
          a: [
            "PatchWall (com.mitv.tvhome) prend le contrôle de la touche Accueil via une liste blanche du système. Une fois désactivé, le lanceur de secours du système refuse lui aussi les lanceurs tiers : la TV démarre sur un écran noir.",
            "canvasOS utilise plutôt la « Protection de l'accueil » : dès que le lanceur d'origine passe au premier plan, canvasOS revient (environ une demi-seconde de clignotement), sans désactiver ni désinstaller aucune application système.",
          ],
        },
        {
          q: "L'installation échoue sur TCL / FFalcon avec INSTALL_FAILED_VERIFICATION_FAILURE",
          a: [
            "C'est la vérification d'installation de TCL qui refuse les installations par adb. Utilisez canvasOS-guard.apk et envoyez-le via le service « Interaction multi-écran (多屏互动) » de la TV ; les commandes sont dans l'onglet TCL ci-dessus.",
            "Si le script ne peut pas joindre le port 6553, ce modèle n'a pas ce service : poussez l'APK dans /sdcard/Download/, puis installez-le avec un gestionnaire de fichiers sur la TV.",
          ],
        },
        {
          q: "Pourquoi n'y a-t-il pas de curseur de luminosité ?",
          a: [
            "Android n'a pas d'interface standard pour régler le rétroéclairage. Sony BRAVIA fournit la luminosité via sa propre interface de contrôle IP ; le firmware chinois de Xiaomi permet d'écrire picture_backlight (autorisation ADB requise). Pour les autres marques, si cela échoue, le curseur est masqué plutôt que d'afficher un contrôle sans effet.",
          ],
        },
        {
          q: "Pourquoi le volume est-il incorrect avec un amplificateur / ARC, eARC ?",
          a: [
            "Avec l'interface de volume Android standard, c'est peut-être la TV elle-même qui est contrôlée plutôt que l'amplificateur. Sur Sony, on peut passer par le volume côté TV ; le comportement du volume CEC dépend en fin de compte des firmwares de la TV et de l'amplificateur.",
          ],
        },
        {
          q: "ADB affiche toujours unauthorized, que faire ?",
          a: [
            "La TV n'a pas accepté l'autorisation de débogage de cet ordinateur. Sur la TV, cochez « Toujours autoriser sur cet ordinateur » puis choisissez « Autoriser » ; les TV Xiaomi coupent ADB à chaque réveil, et l'option « Garder le débogage ADB activé » de canvasOS le réactive automatiquement.",
          ],
        },
        {
          q: "La Protection de l'accueil lit-elle mon écran ou mes notifications ?",
          a: [
            "Non. La protection n'observe que les changements de fenêtre, pour savoir si le lanceur d'origine est passé au premier plan, sans lire le contenu de l'écran ; l'écoute des notifications sert uniquement à relancer canvasOS après un arrêt forcé par le système, et aucune notification n'est lue.",
          ],
        },
        {
          q: "Comment passer à une nouvelle version ?",
          a: [
            "Téléchargez le dernier APK et installez-le par-dessus avec adb install -r : réglages et disposition sont conservés. Tous les liens de téléchargement pointent vers releases/latest sur GitHub, jamais vers une version périmée.",
          ],
        },
        {
          q: "Le site et l'app collectent-ils des données ?",
          a: [
            "Le site est entièrement statique : pas de cookies, pas de statistiques ni de publicité tierces, et les polices sont celles du système.",
            "L'app a besoin d'Internet pour récupérer les fonds d'écran (Bing, Wallhaven) et les contenus du haut (API web publiques de Bilibili et Douyu) ; avec l'économiseur d'écran aérien, les vidéos sont lues en ligne depuis les serveurs d'Apple. Aucun compte n'est nécessaire.",
          ],
        },
        {
          q: "Y a-t-il un lien avec Apple ?",
          a: [
            "Non. canvasOS est un projet open source indépendant, sans lien avec Apple, Sony, Xiaomi, TCL, Bilibili ou Douyu, ni approuvé par eux. Son langage visuel s'inspire de tvOS, mais il ne contient aucun code, police ou ressource d'Apple.",
          ],
        },
      ],
    },

    finalCta: {
      eyebrow: "GET CANVASOS",
      title: "Pour que chaque allumage de la TV soit un plaisir.",
      body: "Téléchargez l'APK, activez ADB, et laissez le guide ou un agent faire le reste. Android 9+, sans root, retour à l'accueil d'origine à tout moment.",
      primary: "Télécharger gratuitement",
      secondary: "Voir le code sur GitHub",
    },

    footer: {
      blurb:
        "Il remplace l'accueil de l'Android TV par le verre et les vues aériennes façon tvOS.",
      cols: [
        {
          title: "Produit",
          links: [
            { href: "#features", label: "Fonctionnalités" },
            { href: "#aerial", label: "Vues aériennes" },
            { href: brand.releases, label: "Versions" },
            { href: brand.repo, label: "GitHub" },
          ],
        },
        {
          title: "Installation",
          links: [
            { href: "#install", label: "Guide d'installation" },
            { href: "#compatibility", label: "Compatibilité" },
            { href: "#faq", label: "FAQ" },
            { href: brand.agents, label: "AGENTS.md" },
          ],
        },
        {
          title: "Projet",
          links: [
            { href: brand.readme, label: "README" },
            { href: brand.issues, label: "Signaler un problème" },
            { href: brand.notices, label: "Mentions tierces" },
          ],
        },
      ],
      legal:
        "© 2026 canvasOS · Projet open source indépendant. Sans lien avec Apple, Sony, Xiaomi, TCL, Bilibili ou Douyu, ni approuvé ou autorisé par eux ; « Apple TV » est une marque d'Apple Inc., citée ici uniquement pour décrire l'économiseur d'écran aérien facultatif. Les affiches de films, photos et icônes d'applications visibles sur les captures appartiennent à leurs ayants droit respectifs. Les films aériens sont fournis par les serveurs d'Apple ; ce projet ne contient aucun fichier vidéo. L'installation et l'utilisation se font à vos propres risques.",
      langLink: { href: "/en/", label: "English", lang: "en" },
    },
} satisfies Copy;
