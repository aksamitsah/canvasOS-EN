#!/usr/bin/env python3
"""Upload only this fork's signing secrets to its GitHub Actions secret store."""
from pathlib import Path
import base64
import subprocess

root=Path(__file__).resolve().parents[1]
props=dict(line.split('=',1) for line in (root/'keystore.properties').read_text().splitlines() if '=' in line and not line.startswith('#'))
key=Path(props['storeFile'])
if not key.is_absolute():key=root/key
values={
    'CANVASOS_KEYSTORE_B64':base64.b64encode(key.read_bytes()).decode(),
    'CANVASOS_KEYSTORE_PASSWORD':props['storePassword'],
    'CANVASOS_KEY_ALIAS':props['keyAlias'],
    'CANVASOS_KEY_PASSWORD':props['keyPassword'],
}
for name,value in values.items():
    subprocess.run(['gh','secret','set',name,'--repo','aksamitsah/canvasOS-EN'],input=value,text=True,check=True)
print('Configured English signing secrets; no key material printed.')
