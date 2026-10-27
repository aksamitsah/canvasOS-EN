#!/usr/bin/env python3
"""Catch French UI regressions and unsafe upstream update routing in the English edition."""
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
TOKEN = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|/\*[\s\S]*?\*/|//[^\n]*')
FRENCH = re.compile(r'[àâçéèêëîïôùûüœ]|\b(?:Accueil|Annuler|Commencer|Paroles|Réglages|Veille|Profils|Horloge|Masquer|Renommer|Supprimer|Dossier|Apparence|requise|Actuelle|Aucun|Aucune|Toujours|téléviseur)\b')
errors = []
count = 0
for p in (ROOT / 'app/src/main/java').rglob('*.kt'):
    for m in TOKEN.finditer(p.read_text()):
        if not m.group().startswith('"'):
            continue
        count += 1
        # A French administrative region can occur as example/data, not UI copy.
        if FRENCH.search(m.group()) and 'Île-de-France' not in m.group():
            errors.append(f'{p.relative_to(ROOT)}:{p.read_text()[:m.start()].count(chr(10)) + 1}: {m.group()[:180]}')
web = (ROOT / 'app/src/main/assets/web/index.html').read_text()
if '<html lang="en">' not in web:
    errors.append('Phone page must declare English.')
# Ignore script character-transliteration data and comments, which intentionally contain accents.
visible = web.split('<script>')[0]
if FRENCH.search(visible.replace('(é becomes e)', '')):
    errors.append('French copy remains in the phone page markup.')
updater = (ROOT / 'app/src/main/java/com/getisolated/canvasos/Updater.kt').read_text()
if 'private const val REPO = "aksamitsah/canvasOS-EN"' not in updater:
    errors.append('Updater must use the English fork.')
for p in (ROOT / 'app/src/main').rglob('*.kt'):
    if 'Locale.FRANCE' in p.read_text() or 'Locale.FRENCH' in p.read_text():
        errors.append(f'{p.relative_to(ROOT)} uses a French display locale.')
if errors:
    print('\n'.join(errors), file=sys.stderr)
    sys.exit(1)
print(f'English UI checks passed ({count} Kotlin string tokens; phone markup, locale and updater checks).')
