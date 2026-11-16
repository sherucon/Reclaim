# Reclaim

A calmer phone.

Reclaim blocks short-form video inside social apps, blocks the apps and evenings you choose, and filters distracting sites system-wide with a local DNS VPN. Free, open source, no account.

Status: Phase 0. The shell, the design tokens, the two build flavors, and the telemetry plumbing exist. Nothing blocks anything yet. The full plan is in `IMPLEMENTATION_PLAN.md`.

## Build

Requires JDK 17 and Android SDK platform 36.

```sh
export JAVA_HOME=/path/to/jdk-17
./gradlew assemblePlayDebug
```

Flavors:

- `play`: Firebase Analytics and Crashlytics, and from Phase 9 AdMob. Google Play build.
- `libre`: identical app with no proprietary SDKs.

Telemetry needs config. Copy `app/google-services.json.example` to `app/google-services.json` and fill in real values from the Firebase console; the file is gitignored. Without it both flavors build and run, the app just uses no-op telemetry.

```sh
./gradlew assemblePlayDebug assembleLibreDebug
./gradlew ktlintCheck lintPlayDebug testPlayDebugUnitTest
```

## Verify telemetry

With `app/google-services.json` in place:

```sh
adb install -r app/build/outputs/apk/play/debug/app-play-debug.apk
adb shell setprop debug.firebase.analytics.app dev.reclaim.app
adb shell am start -n dev.reclaim.app/.MainActivity

# A crash, for Crashlytics. The component is explicit because HyperOS drops
# implicit broadcasts from the shell.
adb shell am broadcast -n dev.reclaim.app/.debug.DebugCrashReceiver -a dev.reclaim.app.DEBUG_CRASH
```

Analytics DebugView and Crashlytics both update within a minute. Release builds
send nothing until the user opts in.

## Privacy

Analytics and crash reports are off until the user opts in. No names, package names, domains, URLs, page content, or notification content ever leave the device. See `IMPLEMENTATION_PLAN.md` section 7.

## License

GPL-3.0, see `LICENSE`.

The UI uses PT Serif, licensed under the SIL Open Font License 1.1. The license text is in `docs/licenses/PTSerif-OFL.txt`.

## Acknowledgements

The design review checklist is adapted from [unslop](https://github.com/heydamir/unslop) by Damir Sokolovsky ([@heydamir](https://x.com/heydamir)), licensed CC BY 4.0.
