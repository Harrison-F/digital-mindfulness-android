# Digital Mindfulness Android APK + Obtainium plan

## Decision

Build the mobile tracker as a native Android APK for GrapheneOS, distributed through GitHub Releases and installed/updated with Obtainium.

Use a separate sync backend later for visit-count data. Obtainium/GitHub Releases is only for app binary updates; it is not the data-sync mechanism.

## Current MVP

- App name: **Digital Mindfulness**
- Package ID: `org.freedomtech.accountability`
- Distribution target: GrapheneOS / Android via sideloaded APK
- Browser detection target packages:
  - `app.vanadium.browser`
  - `com.brave.browser`
  - `com.android.chrome`
  - `org.mozilla.firefox`
  - `org.mozilla.focus`
- Tracking model: local-first visit counts by local calendar day.
- Current tracked domains: `x.com`, `twitter.com`, `youtube.com`, `m.youtube.com`, `youtu.be`.

## Visit semantics

The app counts visits, not time.

A new visit is counted when:

- a supported browser is foregrounded and a tracked URL/domain is visible;
- the visible URL/domain changes;
- the browser/tracked page was left and then reopened;
- the local calendar day changes.

Repeated Accessibility events for the same open tab + same URL on the same local day do **not** increment the count.

## Data sync direction, deferred

Preferred future sync architecture:

- Open-source, self-hostable sync backend.
- Clients remain local-first and queue events if offline.
- Desktop extension and Android APK both push visit increments/rollups to the backend.
- Backend merges by account, device, local day, and domain/URL identity.
- Sync cadence target:
  - push immediately on counted visit when online;
  - periodic retry/flush in the background;
  - refresh dashboard totals frequently while the app/dashboard is open.

Candidate backend stack to choose later:

- small FastAPI/SQLite or Postgres service in Docker;
- PocketBase;
- Supabase-compatible self-hosted Postgres/API;
- Cloudflare Worker/D1 only if self-hosting becomes less important.

## Obtainium update flow

1. Publish a signed APK asset on a GitHub Release.
2. On GrapheneOS, install Obtainium.
3. Add the GitHub repository URL in Obtainium.
4. Configure Obtainium to watch GitHub Releases / APK assets.
5. Install the APK from Obtainium.
6. Future APK releases must use the same signing key and higher `versionCode`.

## Current build command

```bash
cd android
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export ANDROID_SDK_ROOT=/opt/homebrew/share/android-commandlinetools
export PATH="$ANDROID_HOME/platform-tools:$JAVA_HOME/bin:$PATH"
gradle :app:assembleDebug
```

Current local artifact:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

For a real long-lived Obtainium channel, switch from the debug signing key to a dedicated release signing key before broad use.
