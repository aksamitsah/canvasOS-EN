/* UI strings that live in the DOM as attributes or script text rather than in
   the page copy. Keeping them here is what makes the shared client script
   speak French on / and English on /en/. */
import type { Locale } from "./lang";

export interface UiStrings {
  skip: string;
  navLabel: string;
  menuLabel: string;
  menuCloseLabel: string;
  copy: string;
  copied: string;
  copyFailed: string;
  compareName: string;
  compareValue: (p: number) => string;
  prev: string;
  next: string;
  close: string;
  viewer: string;
  pauseClips: string;
  playClips: string;
}

export const uiStrings: Record<Locale, UiStrings> = {
  zh: {
    skip: "Aller au contenu principal",
    navLabel: "Navigation principale",
    menuLabel: "Ouvrir le menu",
    menuCloseLabel: "Fermer le menu",
    copy: "Copier",
    copied: "Copié",
    copyFailed: "Échec de la copie",
    compareName: "Comparer l'illustration d'un lanceur générique avec une capture réelle de canvasOS",
    compareValue: (p) => `${p} % de l'image « avant » affichés`,
    prev: "Précédente",
    next: "Suivante",
    close: "Fermer",
    viewer: "Visionneuse de captures",
    pauseClips: "Mettre en pause les enregistrements",
    playClips: "Lire les enregistrements",
  },
  en: {
    skip: "Skip to content",
    navLabel: "Main",
    menuLabel: "Open menu",
    menuCloseLabel: "Close menu",
    copy: "Copy",
    copied: "Copied",
    copyFailed: "Copy failed",
    compareName: "Compare the generic launcher illustration with a real canvasOS capture",
    compareValue: (p) => `Showing ${p}% of the before image`,
    prev: "Previous",
    next: "Next",
    close: "Close",
    viewer: "Screenshot viewer",
    pauseClips: "Pause recordings",
    playClips: "Play recordings",
  },
};
