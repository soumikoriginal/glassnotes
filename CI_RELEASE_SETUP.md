# GitHub Actions release build — one-time setup

The workflow at `.github/workflows/release.yml` builds a signed release
APK + AAB and, when you push a version tag, publishes them as a GitHub
Release. It needs a signing keystore, provided via repository secrets
(never committed to the repo).

## 1. Generate a release keystore (once, on your own machine)

Requires a JDK installed (`keytool` ships with it).

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias glassnotes \
  -keyalg RSA -keysize 2048 -validity 10000
```

You'll be asked to set a keystore password, a key password, and some
name/org details. **Save the passwords somewhere safe — if you lose them
you cannot update the app under the same signature again.**

## 2. Base64-encode the keystore

```bash
base64 -w 0 release.keystore > release.keystore.base64.txt
```

(On macOS, drop `-w 0`: `base64 -i release.keystore -o release.keystore.base64.txt`)

## 3. Add repository secrets

In GitHub: **Settings → Secrets and variables → Actions → New repository
secret**. Add these four:

| Secret name          | Value                                             |
|-----------------------|----------------------------------------------------|
| `KEYSTORE_BASE64`      | Contents of `release.keystore.base64.txt`          |
| `KEYSTORE_PASSWORD`    | The keystore password you set in step 1            |
| `KEY_ALIAS`            | `glassnotes` (or whatever alias you used)          |
| `KEY_PASSWORD`         | The key password you set in step 1                 |

Delete `release.keystore` and `release.keystore.base64.txt` from your
machine's working folder once added (or keep them somewhere safe outside
the repo) — **do not commit either file.**

## 4. Trigger a build

- Push a tag: `git tag v1.0.0 && git push origin v1.0.0` → builds, signs,
  and publishes a GitHub Release with the APK + AAB attached.
- Or run it manually anytime from the **Actions** tab → *Android Release
  Build* → *Run workflow* (this builds artifacts but does not create a
  GitHub Release).

If the secrets aren't set yet, the workflow still runs and produces an
**unsigned** release build (with a warning in the log) so the rest of the
pipeline can be tested before signing is configured.

## Local release builds (optional)

Copy `app/keystore.properties.example` to `app/keystore.properties`,
fill in your real values, and it'll be picked up automatically by
`app/build.gradle.kts` — no environment variables needed locally.
