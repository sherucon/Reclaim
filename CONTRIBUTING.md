# Contributing

## Setup

1. Install JDK 17 and Android SDK platform 36.
2. Point `local.properties` at your SDK, or set `ANDROID_HOME`.
3. Run `./gradlew assembleLibreDebug` once to make sure the toolchain works.

## Before opening a pull request

```sh
./gradlew ktlintCheck lintPlayDebug testPlayDebugUnitTest testLibreDebugUnitTest
```

CI runs the same commands on both flavors.

## Ground rules

- Keep every color, radius, spacing value, and shadow in `design/`. Screens compose the shared components instead of inventing styles.
- Keep block decisions in pure functions under `domain/` so they stay testable without Android.
- Never log or send names, package names, domains, URLs, page content, or notification content.
- Never add an ad, permission, or SDK without updating the plan, the Data Safety notes, and the privacy statement in the same change.
- The `libre` flavor must keep building with no proprietary dependency on its classpath.

## Commit messages

Plain imperative subject, no agent or tool attribution.
