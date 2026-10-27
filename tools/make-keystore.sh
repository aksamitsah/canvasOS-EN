#!/usr/bin/env bash
# Create a stable English release key. Never print passwords or key material.
set -eu
cd "$(dirname "$0")/.."
python3 - "$@" <<'PY'
from pathlib import Path
import argparse, os, secrets, shutil, subprocess
p = argparse.ArgumentParser(description='Create a stable canvasOS EN release signing key (secrets are never printed).')
p.add_argument('-o', '--output', default='canvasos-en-release.jks')
p.add_argument('-b', '--bits', choices=[2048,4096], type=int, default=4096)
p.add_argument('--properties', action='store_true', help='also create ignored keystore.properties')
a=p.parse_args()
key=Path(a.output).resolve(); props=Path('keystore.properties')
if key.exists() or (a.properties and props.exists()):
    p.error('Refusing to overwrite an existing key or signing properties.')
keytool=shutil.which('keytool')
if os.environ.get('JAVA_HOME'):
    candidate=Path(os.environ['JAVA_HOME'])/'bin/keytool'
    if candidate.is_file(): keytool=str(candidate)
if not keytool:p.error('Install a JDK and set JAVA_HOME first.')
password=os.environ.get('CANVASOS_KEYSTORE_PASSWORD') or secrets.token_urlsafe(36)
if len(password)<6 or '\\' in password or password != password.strip():p.error('Password must be 6+ characters, without backslashes or edge spaces.')
env=dict(os.environ,EN_KEYSTORE_PASSWORD=password)
r=subprocess.run([keytool,'-genkeypair','-keystore',str(key),'-storetype','PKCS12','-storepass:env','EN_KEYSTORE_PASSWORD','-alias','canvasos-en','-keyalg','RSA','-keysize',str(a.bits),'-validity','10000','-dname','CN=canvasOS EN, O=aksamitsah'],env=env,capture_output=True,text=True)
if r.returncode:raise SystemExit('Key generation failed. Check JAVA_HOME and output directory.')
key.chmod(0o600)
if a.properties:
    props.write_text(f'storeFile={key}\nstorePassword={password}\nkeyAlias=canvasos-en\nkeyPassword={password}\n');props.chmod(0o600)
else:
    secret=key.with_suffix('.password');secret.write_text(password+'\n');secret.chmod(0o600)
    print(f'Password saved privately to {secret.name}; keep it out of Git.')
print(f'Created {key.name}. Back up the key and password securely; never commit them.')
print('Use tools/configure-release-secrets.py to configure the repository without printing secrets.')
PY
