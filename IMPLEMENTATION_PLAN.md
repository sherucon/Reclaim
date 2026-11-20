# Reclaim - Native Android Implementation Plan

Status: Draft v3, awaiting approval.
Date: 2026-11-16.
Working directory: `/Users/sher/Development/reclaim`.
Reference target: SocialLite 3.0.1 (`com.sociallite.android`) pulled from the connected Redmi device.
Visual north star: the attached soft, warm, iOS-like reference (off-white canvas, floating cards, pastel charts, 3D character). We copy the mood, not the assets or brand.

## 0. Changes from v2

### 0.1 Monetization

- The app now earns money through one non-invasive sponsor card at a time, never through banners, interstitials, app-open ads, or rewarded video.
- Sponsor cards are real ads (Google AdMob native format) rendered inside our own `SoftCard`, so they share the app's radius, padding, shadow, and type instead of looking like a foreign rectangle.
- Every sponsor card carries a `Sponsored` label and the mandatory AdChoices control. It can feel native; it may not pretend to be Reclaim content.
- Placement is limited to two low-intent surfaces: the bottom of Home and the bottom of About. Never in onboarding, the app picker, the web view, Settings, widgets, or the block overlay.
- Frequency is capped locally: nothing until onboarding is complete, at most one impression per session, at most three per day, twenty minutes between impressions, and nothing at all on no-fill or offline.
- A `libre` build flavor ships the identical app with no Firebase, no AdMob, and no ads, so the repository stays fully open source and F-Droid viable.
- Billing stays out of scope: no subscription, no paid removal, no paywall. Ads are the only revenue and every feature stays free.

### 0.2 Measurement from day one

- Firebase project setup and the analytics plumbing moved from the final phase into Phase 0.
- Every later phase lands with events already visible in DebugView, so the funnel is measurable from the first usable build instead of at the end.
- Release builds stay consent-gated. Debug builds get a developer-only override so DebugView works before the Phase 3 consent screen exists.

### 0.3 Carried from v2

- Analytics and crash reporting are opt-in, default off, behind interfaces.
- Visual system, conversational onboarding, and Play launch are fully specified.
- Billing and kid/parent sync remain out of scope.

## 1. Purpose

Build a free, open-source, native Android app inspired by SocialLite's behavior.

The app helps a person use social media deliberately by combining three controls:

1. Blocking short-form video surfaces inside native social apps.
2. Blocking selected apps outright and during a daily downtime window.
3. Blocking distracting websites system-wide via a local DNS-filtering VPN.

It also ships home-screen widgets and a built-in, distraction-reduced web experience for each platform.

It is instrumented with privacy-respecting product analytics so the app can be improved after launch.

This plan covers features 1, 2, 3, 5, and 7 from the SocialLite analysis, plus analytics, a capped sponsor slot, and Play launch.

It explicitly excludes feature 4 (billing/subscriptions) and feature 6 (kid/parent linking and backend sync).

### 1.1 Product principles

- Local-first: no account, no backend database, no cloud sync of user settings.
- Analytics with consent: anonymous product analytics and crash reports, off by default until the user opts in.
- Free to use: no paywall, no trial, no subscription, no RevenueCat, no Play Billing. Every feature stays free.
- Quietly funded: at most one sponsor card at a time, rendered in the app's own visual language, never inside a blocking moment.
- Native Android: Kotlin and Jetpack Compose, no Flutter, React Native, or WebView-based app shell.
- Premium feel: every screen is designed against the visual system in section 5, not assembled from default Material components.
- Conversational onboarding: short personalized questions, not a wall of toggles.
- Clean-room: implement behavior from observation, do not copy SocialLite code, assets, strings, or branding.
- Explainable permissions: every sensitive permission is requested from a dedicated card with a plain-language reason and a visible system-state indicator.
- Fail open for system usability: never lock the user out of Settings, the launcher, the IME, or Reclaim itself.
- Smallest complete implementation: no SDK sprawl beyond Firebase Analytics, Crashlytics, AdMob, and the UMP consent library; no DI framework; no premature modularization.

### 1.2 Scope mapping

| Capability | In scope | Plan section |
|---|---|---|
| 1. Reels/Shorts content blocker | Yes | 4.1 |
| 2. App blocking engine | Yes | 4.2 |
| 3. Web blocker VPN (DNS) | Yes | 4.3 |
| 4. Billing and paywall | No | Explicitly excluded |
| 5. Widgets | Yes | 4.4 |
| 6. Kid/parent linking and sync | No | Explicitly excluded |
| 7. Overall architecture and in-app web experience | Yes | 4.5 |
| Product analytics and crash reporting | Yes, opt-in | 7 |
| Non-invasive sponsor cards (native ads) | Yes, capped | 4.6 |
| Build flavors: `play` and `libre` | Yes | 3.1, 11 |
| Premium visual system and conversational onboarding | Yes | 5 and 6 |
| Google Play launch | Yes | 8 |

### 1.3 Non-goals for v1

- No user accounts, login, cloud sync, or device pairing.
- No parental controls or child profiles.
- No banner ads, interstitials, app-open ads, rewarded video, or ads that autoplay with sound.
- No ads in onboarding, the app picker, the web view, Settings, widgets, or the block overlay.
- No paid ad removal, no feature gates; ads never lock or unlock a feature.
- No data selling and no cross-app tracking of our own; ad personalization runs only through Google's consent framework and degrades to non-personalized ads.
- No usage statistics dashboard based on `PACKAGE_USAGE_STATS` (not needed for blocking).
- No iOS app.
- No root-only features.
- No remote config, A/B testing, or feature flags in v1.
- No dark theme in v1, to protect the off-white visual identity; a warm charcoal theme is planned for v1.1.
- No guarantee against deliberate bypass: DoH/DoT, secondary devices, and browsers can bypass DNS filters.
- No copying of SocialLite's obfuscated code, JavaScript assets, illustrations, or brand.

## 2. Decisions to confirm before implementation

| # | Decision | Proposed default | Why it matters |
|---|---|---|---|
| D1 | App name | Reclaim | Matches the repository name and the product idea. |
| D2 | Application ID | `dev.reclaim.app` | Stable, neutral, easy to rename before first release. |
| D3 | License | GPL-3.0 | Keeps derivatives open, which fits a free blocker app. Apache-2.0 is the permissive alternative. |
| D4 | Minimum SDK | API 29 (Android 10) | Covers modern devices while keeping implementation simple. SocialLite uses API 32. |
| D5 | v1 platform set | Instagram, YouTube, TikTok, Facebook, Snapchat, X, LinkedIn, Reddit | Covers native blockers, widgets, and the web layer with one registry. |
| D6 | Native blocker depth | Instagram, YouTube, TikTok primary; Facebook and Snapchat beta | View-ID detection is reliable on IG/YT. Facebook/Snapchat need scroll heuristics. |
| D7 | Notification suppression | Included, off by default | Completes app blocking, adds a notification-listener permission. |
| D8 | Emergency pause | Included, 5 minutes, confirm dialog, from settings only | Prevents lockout while keeping the block overlay non-bypassable. |
| D9 | Analytics stack | Firebase Analytics plus Firebase Crashlytics | Free, Play-native, DebugView, retention cohorts, no backend to maintain. |
| D10 | Analytics consent | Explicit opt-in, default off, in onboarding and Settings | Satisfies GDPR/ePrivacy and keeps Data Safety honest. |
| D11 | Crash reporting consent | Follows the same analytics toggle | One clear privacy switch is easier to understand than three. |
| D12 | Theme | Light-only in v1 | Protects the off-white aesthetic and reduces polish surface. |
| D13 | Typography | PT Serif (SIL OFL) | Editorial serif with a printed-page calm that matches the reference mood without licensing cost. |
| D14 | Icon system | Font Awesome Free (Solid), 24 dp glyphs | One font carries every icon; filled shapes hold up inside pastel circles and need no stroke tuning. |
| D15 | Mascot | None | The app carries personality through type, color, and object illustrations, without a character to maintain. |
| D16 | Distribution | Google Play first, GitHub Releases as backup | Play needs declarations for accessibility, VPN, and foreground services. |
| D17 | Play target audience | 13+ | A social media control app is not a children's app. |
| D18 | Platform icons | Own monochrome glyphs and letter tiles | Avoids copying trademarked logos and SocialLite assets. |
| D19 | Ad format | AdMob native ads rendered in our own card; no banners, interstitials, or app-open ads | Native is the only format that can look like part of the UI without breaking ad policy. |
| D20 | Ad surfaces | Bottom of Home plus bottom of About; one per session, three per day | Keeps ads out of every decision, permission, and blocking moment. |
| D21 | Ad labeling | `Sponsored` overline plus the mandatory AdChoices control | Policy requires clear identification; styling can be native, disclosure cannot be hidden. |
| D22 | Ad consent | Google UMP form, non-personalized ads when consent is absent | Required for EEA and UK traffic, and keeps the Data Safety form accurate. |
| D23 | Build flavors | `play` (Firebase, AdMob, UMP) and `libre` (NoOp, no proprietary SDKs) | Keeps the open-source build clean and F-Droid viable. |
| D24 | Paid ad removal | Not in v1 | Billing stays out of scope; the answer to ad fatigue is strict caps, not a paywall. |
| D25 | Ads during development | Google test ad units only until Phase 9; production units injected by CI | Tapping your own live ads is invalid traffic and can get the AdMob account banned. |
| D26 | Design review | unslop checklist, adapted, bold display type kept | Adds a proven craft pass without flattening the direction into a neutral template. |

If any default is wrong, change it before Phase 0.

## 3. Technical architecture

### 3.1 Stack

| Concern | Choice |
|---|---|
| Language | Kotlin, latest stable supported by the chosen AGP |
| UI | Jetpack Compose with Material 3 foundations, custom Reclaim component layer |
| Navigation | Horizontal pager for the four tabs, Navigation Compose for detail screens, single activity |
| State | ViewModel plus StateFlow, immutable UI state |
| Persistence | DataStore Preferences for settings and local counters, no Room in v1 |
| Dependency injection | Manual `AppContainer` created in `Application`, no Hilt |
| Async | Kotlin coroutines and Flow |
| Scheduling | In-service tick plus `AlarmManager`/`WorkManager` only where proven necessary |
| Web | Android `WebView` with local JS/CSS assets |
| Widgets | Classic `RemoteViews` providers with XML layouts |
| Analytics | Firebase Analytics (GA4) behind an `AnalyticsTracker` interface |
| Crash reporting | Firebase Crashlytics behind the same consent switch |
| Ads | Google AdMob native ads behind an `AdProvider` interface |
| Ad consent | Google UMP consent form with a non-personalized fallback |
| Build flavors | `play` (Firebase, AdMob, UMP) and `libre` (NoOp, no proprietary SDKs) |
| Networking | `WebView`, DNS forwarding, and the Google SDKs; no app HTTP client |
| Tests | JUnit, kotlinx-coroutines-test, Robolectric, Compose UI tests, Roborazzi screenshot tests |
| CI | GitHub Actions: build, unit tests, lint, ktlint, screenshot tests |
| Play | AAB, Play App Signing, internal testing, staged rollout |

Firebase and AdMob sit behind `AnalyticsTracker`, `CrashReporter`, and `AdProvider` so the `libre` flavor compiles with NoOp implementations and no proprietary SDK. The `google-services` plugin and the AdMob dependency apply per flavor, so a fresh checkout with no config files still builds and runs.

### 3.2 Package layout

```
app/src/main/java/dev/reclaim/app/
  ReclaimApplication.kt
  MainActivity.kt
  AppContainer.kt
  analytics/
    AnalyticsTracker.kt
    FirebaseAnalyticsTracker.kt
    NoOpAnalyticsTracker.kt
    AnalyticsEvents.kt
    DailyStatsWorker.kt
    CrashReporter.kt
  ads/
    AdProvider.kt
    AdSlot.kt
    NoAdProvider.kt
    AdMobAdProvider.kt
    AdConsentManager.kt
    AdFrequencyPolicy.kt
    NativeAdCard.kt
  core/
    time/Clock.kt
    util/Result.kt
    counters/DailyCounters.kt
    counters/CounterRepository.kt
  data/
    settings/SettingsRepository.kt
    settings/SettingsKeys.kt
    apps/InstalledAppRepository.kt
    domains/DomainListRepository.kt
  domain/
    model/AppSettings.kt
    model/Platform.kt
    model/PlatformRegistry.kt
    model/DowntimeSchedule.kt
    model/BlockReason.kt
    model/Goal.kt
    blocking/BlockEvaluator.kt
    blocking/ScrollAccumulator.kt
    blocking/ShortVideoDetector.kt
    domains/DomainMatcher.kt
  design/
    ReclaimTheme.kt
    Color.kt
    Type.kt
    Shape.kt
    Space.kt
    components/SoftCard.kt
    components/PillButton.kt
    components/PillToggle.kt
    components/SegmentedPills.kt
    components/StatusRing.kt
    components/MiniBarChart.kt
    components/IconBubble.kt
    components/FloatingDock.kt
    components/PermissionCard.kt
    icons/ReclaimIcons.kt
  service/
    accessibility/AppBlockerService.kt
    accessibility/ContentBlockerService.kt
    accessibility/AccessibilityIds.kt
    notification/NotificationFilterService.kt
    vpn/WebBlockerVpnService.kt
    vpn/DnsMessage.kt
    vpn/DnsResolver.kt
  ui/
    nav/ReclaimNavHost.kt
    home/HomeScreen.kt
    home/HomeViewModel.kt
    apps/AppsScreen.kt
    apps/AppsViewModel.kt
    reels/ReelsScreen.kt
    reels/ReelsViewModel.kt
    webblock/WebBlockerScreen.kt
    webblock/WebBlockerViewModel.kt
    onboarding/OnboardingFlow.kt
    onboarding/OnboardingViewModel.kt
    onboarding/steps/
    schedule/ScheduleScreen.kt
    settings/SettingsScreen.kt
    about/AboutScreen.kt
    overlay/BlockOverlayActivity.kt
    web/WebPlatformScreen.kt
    web/WebViewHost.kt
  widget/
    PlatformWidgetProvider.kt
    QuickLaunchWidgetProvider.kt
    WidgetIntents.kt
```

The design package is the only place that defines colors, type, shape, spacing, shadows, and shared components.

Screens compose those primitives and do not invent one-off styles.

### 3.3 Runtime components

```
MainActivity (Compose)
  |-- Navigation: Onboarding, Home, Apps, Reels, Web Blocker, Schedule, Settings, About
  |-- WebPlatformScreen -> WebView + injected JS
  |-- BlockOverlayActivity (full-screen interruption)
  |
  +-- SettingsRepository (DataStore)
  +-- CounterRepository (local daily counters for in-app charts)
  +-- AnalyticsTracker (Firebase or NoOp) <- consent gate
  +-- AdProvider (AdMob or NoOp) <- UMP consent + local frequency caps
  |
  +-- AppBlockerService (Accessibility)
  |     | foreground package events
  |     |-- BlockEvaluator -> BlockOverlayActivity (app block)
  |     +-- schedule tick
  |
  +-- ContentBlockerService (Accessibility)
  |     | platform gate + ShortVideoDetector
  |     |-- ScrollAccumulator -> BlockOverlayActivity (reels break)
  |
  +-- WebBlockerVpnService (VpnService)
  |     |-- DnsMessage parser/matcher
  |     +-- upstream forwarding
  |
  +-- NotificationFilterService (NotificationListener, optional)
  +-- DailyStatsWorker (aggregates counters, sends one daily analytics event)
  |
  +-- Widgets -> PendingIntent -> reclaim://open?platform=<key>
```

### 3.4 Settings and local data model

DataStore Preferences keys:

| Key | Type | Default |
|---|---|---|
| `onboarding_complete` | Boolean | false |
| `display_name` | String | empty, local only, never sent to analytics |
| `primary_goals` | String set | empty |
| `onboarding_step` | String | empty, the resume point |
| `analytics_enabled` | Boolean | false |
| `app_blocking_enabled` | Boolean | false |
| `blocked_packages` | String set | empty |
| `downtime_enabled` | Boolean | false |
| `downtime_days` | Int bitmask | every day |
| `downtime_start_minutes` | Int | 22:00 |
| `downtime_end_minutes` | Int | 07:00 |
| `pause_until_epoch_ms` | Long | 0 |
| `notifications_suppressed` | Boolean | false |
| `content_blocking_enabled` | Boolean | false |
| `content_platforms` | String set | `instagram`, `youtube`, `tiktok` |
| `content_sensitivity` | Enum string | balanced |
| `content_cooldown_seconds` | Int | 30 |
| `web_blocking_enabled` | Boolean | false |
| `web_categories` | String set | `social` |
| `web_custom_domains` | String set | empty |
| `counter_days` | String set | JSON daily buckets, pruned to 90 days |
| `last_stats_upload_day` | String | empty |
| `theme_mode` | Enum string | light |
| `last_web_blocked_count` | Int | 0 |
| `ads_last_shown_epoch_ms` | Long | 0 |
| `ads_impressions_today` | String | JSON `day` plus `count`, reset on day change |

Rules:

- Package names are stored locally, labels are resolved live from `PackageManager`.
- Custom domains are normalized before storage (lowercase, trim trailing dot, strip scheme and path, reject IP literals).
- The display name stays on device and is never attached to an analytics event.
- Blocked package names and custom domains are never sent to analytics.
- A single settings flow feeds all services; services collect the flow and evaluate on each event.
- Ad frequency state stays local; the advertising ID never enters DataStore or any Firebase event.

### 3.5 Pure decision layer

All block decisions are pure functions so they are unit-testable without Android services.

```
fun BlockEvaluator.shouldBlockApp(
  packageName: String,
  now: ZonedDateTime,
  settings: AppSettings,
  paused: Boolean
): BlockDecision

fun Schedule.isActive(now: ZonedDateTime): Boolean

fun ScrollAccumulator.add(
  deltaY: Int,
  screenHeightPx: Int,
  surfaceActive: Boolean
): ScrollState

fun DomainMatcher.classify(host: String): DomainCategory?
```

Overnight schedule semantics: the day bit refers to the day the window starts.

Example: Monday enabled with 22:00-07:00 blocks Monday 22:00 to Tuesday 07:00.

### 3.6 Service lifecycle

- Accessibility services are enabled by the user in system settings; no app-side start/stop.
- Each service reads settings on connect and collects changes.
- Services never hold `AccessibilityNodeInfo` beyond the event callback; nodes are recycled in `finally`.
- VPN runs as a foreground service with a persistent notification while active.
- The VPN excludes Reclaim's own package so the in-app web experience keeps working while other apps are filtered.
- Boot restore re-evaluates rules and restores the notification listener; the VPN never auto-starts without prior opt-in because Android requires prepared consent.

### 3.7 Privacy and security

- No third-party SDKs beyond Firebase Analytics, Crashlytics, AdMob, and the UMP consent library.
- Analytics and crash collection are opt-in. Ad personalization is governed by the UMP form, with non-personalized ads as the fallback.
- No data sale and no cross-app tracking beyond what the ad SDK does under Google's policy and the user's consent.
- Ad requests carry no keywords, no content URL, no custom targeting, and no Reclaim data beyond the request itself.
- No analytics event contains names, package names, domains, URLs, page text, or notification content.
- Accessibility services read only what is needed for detection: package name, view IDs, node bounds, scroll deltas, and a short allowlist of chat-safety text markers.
- Node text is never logged or stored.
- Debug logs are compiled out of release builds or gated behind a debug-only flag.
- WebView has file access disabled, mixed content disabled, no JavaScript bridge, and a host allowlist.
- Domain lists are bundled assets plus local user entries; no remote updates.
- Firebase config is supplied per environment and is not committed to the public repository.
- Crashlytics custom keys never include user content or identifiers.

## 4. Feature designs

### 4.1 Feature 1: Reels/Shorts blocker

#### Behavior

While the user scrolls short-form video inside a supported native app, Reclaim detects the surface, accumulates vertical scrolling, and interrupts the session once a per-session budget is exhausted.

The interruption is a full-screen Reclaim overlay that offers "Go home" and "Open Reclaim".

There is no in-overlay dismiss, because dismissing would restore the feed.

#### Supported platforms and signals

| Platform | Package | Strong signal |
|---|---|---|
| Instagram | `com.instagram.android` | View IDs `clips_viewer_container`, `clips_viewer_view_pager`, `clips_viewer_video_layout` |
| YouTube | `com.google.android.youtube` | View IDs `shorts_player_container`, `reel_player_page_container`, `reel_recycler` |
| TikTok | `com.zhiliaoapp.musically`, `com.ss.android.ugc.trill` | Full-screen scroll heuristic plus package gate |
| Facebook | `com.facebook.katana` | Full-screen scroll heuristic, beta |
| Snapchat | `com.snapchat.android` | Full-screen scroll heuristic, beta |

Strong-signal detection:

- On `TYPE_WINDOW_CONTENT_CHANGED`, search the active root for the platform's view IDs.
- Mark the surface active when a matching node is visible and covers at least 60 percent of the screen height.
- Mark it inactive when the user leaves the package or no matching node is visible for 2 seconds.

Scroll heuristic for TikTok/Facebook/Snapchat:

- Only count `TYPE_VIEW_SCROLLED` on nodes covering at least 85 percent of the screen.
- Require vertical delta to dominate horizontal delta.
- Suppress detection for 1 second after a window-state change to avoid navigation noise.
- Treat the surface as active only while consecutive full-screen scroll events arrive.

Chat safety:

- Ignore the session when visible text matches a small allowlist such as "Back to chat", "Reply privately", "message", "conversation", "send".
- This prevents DM and comment screens from being mistaken for Reels.
- The allowlist is matched in memory only and never logged.

#### Scroll budget

Sensitivity presets:

| Preset | Budget | Cooldown |
|---|---|---|
| Relaxed | 3.0 screen heights | 60 s |
| Balanced | 1.5 screen heights | 30 s |
| Strict | 0.8 screen heights | 15 s |

The accumulator resets when:

- The user leaves the platform.
- The screen turns off.
- No qualifying scroll arrives for 60 seconds.
- The overlay was shown and its cooldown is still active.
- The accessibility service reconnects.

When the budget is reached and the cooldown elapsed, launch `BlockOverlayActivity` with reason `SHORTS_LIMIT` and the platform key.

#### Analytics hooks

- `reels_platform_toggled(platform, enabled)`.
- `reels_sensitivity_set(level)`.
- `reels_break_shown(platform, sensitivity, scroll_bucket)`.
- `daily_stats(reels_breaks, app_blocks, dns_blocks)`.
- Never log scroll text, node text, or the content of the feed.

#### Settings UI

- Master toggle.
- Per-platform toggles.
- Sensitivity segmented control with three illustrated cards.
- Cooldown stepper.
- "Test detection" debug card showing the last event, detected surface, accumulated pixels, and reset reason. Debug only.

#### Accessibility service configuration

- Events: `typeWindowStateChanged`, `typeWindowContentChanged`, `typeViewScrolled`, `typeViewClicked`, `typeViewSelected`.
- Flags: `flagIncludeNotImportantViews`, `flagReportViewIds`, `flagRetrieveInteractiveWindows`.
- `canRetrieveWindowContent=true`.
- `notificationTimeout=50`.
- No gestures, no screenshots.

#### Edge cases

- Split screen: only react when the platform window is the active window.
- Picture-in-picture: ignore events from PiP windows.
- App updates change view IDs: log a debug warning and fall back to the scroll heuristic for that platform.
- Comment sheets and Story viewers: chat-safety markers and non-full-screen bounds should keep them safe; verify manually.
- Overlay launch restrictions: if background activity start is refused, fall back to a `SYSTEM_ALERT_WINDOW` overlay with an explicit permission request and document it.

### 4.2 Feature 2: App blocking engine

#### Behavior

The user selects apps to block.

A blocked app is intercepted when it comes to the foreground, either:

- Always, while app blocking is enabled, or
- During the configured downtime window.

The intercept is a full-screen `BlockOverlayActivity` showing the app icon, app name, the active reason, "Go home", and "Open Reclaim".

#### Foreground detection

- `AppBlockerService` listens for `TYPE_WINDOW_STATE_CHANGED` and records the package name.
- Ignored packages: Reclaim itself, `com.android.systemui`, the default launcher, the current IME, and system permission dialogs.
- Settings is intentionally not ignored so the user can always reach system settings; the overlay shows an "Open settings" shortcut instead of blocking Settings.
- Evaluation runs on every event and on a 15-second tick while the service is connected, so a downtime window starting while an app is already open triggers the overlay.

#### Rules

| Rule | Data | Notes |
|---|---|---|
| Always block | `blocked_packages` | Active while `app_blocking_enabled` is true. |
| Downtime | `downtime_*` | One window with day selection, supports overnight. |
| Pause | `pause_until_epoch_ms` | Set from Settings with a confirm dialog; the overlay never offers a pause. |

#### Overlay behavior

- `excludeFromRecents=true`, single instance, no history.
- Back press and recents are disabled while the overlay is frontmost.
- Launch cooldown of 1.5 seconds per package to avoid loop storms.
- If the same blocked package is already behind the overlay, do not stack a second overlay.
- "Go home" starts the HOME intent; "Open Reclaim" launches MainActivity.

#### Notification suppression (optional, default off)

- `NotificationFilterService` extends `NotificationListenerService`.
- When enabled and a blocked app's rule is active, call `cancelNotification(key)` for notifications from that package.
- No notification redirection, rewriting, or content inspection in v1.
- The service is purely best-effort; Android may not deliver all notifications.

#### App picker

- Query launchable activities with `Intent.CATEGORY_LAUNCHER`; no `QUERY_ALL_PACKAGES` permission.
- Show icon, label, and package name.
- Search by label.
- Group social apps from the platform registry at the top, then all apps alphabetically.

#### Analytics hooks

- `feature_toggled(feature = app_blocking, enabled)`.
- `apps_selected_bucket(bucket = 0, 1, 2-4, 5+)`.
- `downtime_set(days_count, has_overnight, enabled)`.
- `block_overlay_shown(kind = app, reason = always, downtime, notification_suppressed)`.
- Never log package names or app labels.

#### Settings UI

- Master toggle.
- App picker with selected count.
- Downtime toggle, day chips, start and end time pickers.
- Notification suppression toggle plus permission card.
- Emergency pause button with a 5-minute confirmation.
- Accessibility service status card with a deep link to system settings.

#### Edge cases

- Service disabled by the user or the OS: dashboard shows a warning; blocking is inactive.
- Reboot: accessibility services re-bind automatically when enabled; no boot receiver needed for blocking.
- Android 14+ sideloaded apps: the user must allow restricted settings before enabling accessibility; onboarding explains this.
- Work profile and cloned apps: blocked by package name; cloned packages may differ and are treated as separate entries.
- Multiple windows: only the active window is evaluated.

### 4.3 Feature 3: Web blocker VPN (DNS filtering)

#### Behavior

While enabled, Reclaim runs a local `VpnService` that intercepts DNS queries and answers NXDOMAIN for domains in enabled categories or the user's custom list.

All other DNS queries are forwarded to the device's upstream resolver.

This blocks sites system-wide without an HTTP proxy or TLS interception.

#### Packet pipeline

```
tun read
  -> IPv4 packet
  -> UDP destination port 53
  -> parse QNAME
  -> normalize host
  -> DomainMatcher
      -> blocked: build NXDOMAIN response, write to tun
      -> allowed: forward via protected DatagramSocket, write reply to tun
```

Implementation details:

- VPN builder: `addAddress("10.111.222.1", 24)`, `addDnsServer("10.111.222.2")`, `addRoute("10.111.222.0", 24)`.
- `addDisallowedApplication(packageName)` keeps Reclaim's WebView outside the filter.
- Upstream resolvers come from `LinkProperties` of the underlying network, with a user-configurable fallback.
- Forward sockets are `protect()`ed so replies do not loop back into the tunnel.
- Per-query timeout of 5 seconds.
- No DNS cache in v1.
- DNS over TCP is not handled; it is dropped, which is acceptable because Android's stub resolver uses UDP first.
- NXDOMAIN responses copy the query ID and question, set QR, RA, and rcode 3, and echo the OPT record when present.

#### Domain lists

Bundled assets:

- `assets/domains/social.txt`
- `assets/domains/adult.txt`
- `assets/domains/gambling.txt`

Matching rules:

- Lowercase, strip trailing dot, strip `www.` prefix.
- Block exact match and all subdomains.
- Ignore IP literals and malformed names.
- Custom user domains use the same matcher.

The list contents are our own curated lists, not copied from SocialLite.

#### Local counters

- Count blocked queries per category in memory.
- Flush to daily buckets every 60 seconds and on service stop.
- Home and Web Blocker screens show the last 7 days.
- Only daily totals are sent to analytics, never individual domains.

#### Analytics hooks

- `feature_toggled(feature = web_blocking, enabled)`.
- `web_category_toggled(category, enabled)`.
- `web_custom_domains_bucket(bucket)`.
- `vpn_started(category_count)`.
- `vpn_revoked`.
- `daily_stats(reels_breaks, app_blocks, dns_blocks)`.
- Never log queried domains, IP addresses, or per-query events.

#### Settings UI

- Master toggle with VPN consent flow via `VpnService.prepare`.
- Status card: running, domain count, queries blocked today.
- Category switches: Social, Adult, Gambling.
- Custom domain editor with validation and deduplication.
- Link to system VPN settings for always-on and "block connections without VPN".
- Explanation that DoH/DoT and private DNS can bypass DNS filtering.

#### Foreground service

- Persistent notification with a stop action.
- `specialUse` foreground service type on Android 14+, with a subtype property.
- `onRevoke` closes the tun and updates UI state.
- Stop action closes tun, stops forwarding threads, and stops the service.

#### Edge cases

- Another VPN is active: `VpnService.prepare` fails or the system chooses one VPN; show a clear message.
- Private DNS: document that system Private DNS may bypass filtering depending on OEM behavior.
- IPv6: v1 routes IPv4 DNS only; verify on the Redmi and add IPv6 interception only if testing shows bypass.
- Apps using their own DoH endpoints bypass the filter; documented limitation.
- Reclaim's own WebView must keep working, hence the disallowed application entry.

### 4.4 Feature 5: Widgets

| Widget | Slots | Opens |
|---|---|---|
| Quick Launch | Instagram, Snapchat, YouTube, TikTok | `reclaim://open?platform=<key>` |
| Instagram | 1 | `reclaim://open?platform=instagram` |
| YouTube | 1 | `reclaim://open?platform=youtube` |
| TikTok | 1 | `reclaim://open?platform=tiktok` |
| X | 1 | `reclaim://open?platform=x` |
| Facebook | 1 | `reclaim://open?platform=facebook` |
| LinkedIn | 1 | `reclaim://open?platform=linkedin` |
| Snapchat | 1 | `reclaim://open?platform=snapchat` |

Design:

- Classic `RemoteViews` with shared layout files.
- White pill cards with soft corners, monochrome glyph inside a pastel circle.
- No official platform logos.
- `updatePeriodMillis=0`; widgets are static and update on package replace.
- PendingIntent immutable and unique per widget/slot.
- No subscription gating.

Deep link handling:

- Manifest intent filter for `reclaim://open`.
- `MainActivity` validates the platform key against `PlatformRegistry`.
- Unknown keys fall back to Home with an error toast.
- The web screen opens the platform home URL and applies the injected script.

Analytics hooks:

- `widget_added(type)` from widget provider when the first update arrives.
- `widget_tapped(type, platform)`.
- No analytics for widget placement counts beyond these events.

### 4.5 Feature 7: In-app web experience

#### Screens

| Screen | Contents |
|---|---|
| Web Platform | WebView for one platform with injected CSS/JS, soft top bar, reload, open in browser, clear data |
| About web mode | Explanation of the distraction-reduced experience and its limitations |

#### WebView configuration

- JavaScript enabled for the platforms.
- DOM storage and cookies enabled for login persistence.
- File and content access disabled.
- Mixed content never allowed.
- Safe Browsing enabled.
- No `addJavascriptInterface`.
- Web contents debugging only in debug builds.
- Permission requests (camera, mic, geolocation) denied by default.
- Multiple windows disabled; OAuth popups open as full-page navigations where possible.

#### Navigation policy

- Allow only the platform's hosts plus a fixed auth-host allowlist.
- External links open in a Custom Tab.
- Block `intent://`, `market://`, `file://`, `content://`, `javascript:`.
- Back button navigates WebView history before leaving the screen.

#### Injected web customizations

Our own small scripts, one per platform, kept idempotent with a namespace guard:

| Platform | Behavior |
|---|---|
| Instagram | Hide Reels and Explore entry points, hide comments panel, hide suggested posts, keep DMs reachable |
| YouTube | Force the mobile site, hide Shorts shelves and home recommendations, disable autoplay |
| X | Hide the For You tab and trends prompts |
| Reddit | Hide Popular and recommended feeds |
| Facebook, TikTok, Snapchat, LinkedIn | Baseline: hide obvious app-install banners and reduce autoplay where selectors allow |

Implementation:

- CSS injection for stable selectors.
- `MutationObserver` for SPA changes.
- Re-apply on same-document navigation.
- Never read or exfiltrate page content.

#### Login limitation

Embedded WebView logins may be rejected by some providers.

The UI explains this and offers "Open in browser" as a fallback, at the cost of losing injected customizations for that session.

#### Analytics hooks

- `web_platform_opened(platform)`.
- `web_external_link_opened(platform)`.
- `web_script_failed(platform, reason_code)`.
- Never log URLs, page titles, or content.

### 4.6 Monetization: sponsor cards (non-invasive ads)

#### Why this format

The app earns money only from native sponsor cards.

| Format | Verdict |
|---|---|
| Anchored banner | Rejected. A foreign rectangle glued to the canvas wrecks the visual system and earns little. |
| Interstitial or app-open ad | Rejected. It interrupts the user and reads as hypocritical in an app whose job is to stop interruptions. |
| Rewarded video | Rejected. There is no honest reward in a blocker; watching an ad to unlock protection would corrupt the product. |
| Native ad in our own card | Chosen. The network supplies icon, headline, body, image, and CTA; we render them with our own components. |

#### What the network requires

Google's native ad policy sets the outer bounds of "it looks like part of the UI":

- A visible ad label. We use a `Sponsored` overline in `inkTertiary`, never hidden, never replaced with an ambiguous word.
- The AdChoices overlay icon, unobstructed, with a 48 dp touch target.
- Every asset registered on the `NativeAdView`, with `recordImpression` and `reportClick` wired. Without that, impressions do not count and the account gets flagged.
- No ad where a tap is likely accidental: not next to a primary CTA, not over a toggle, not under a scroll stop.
- No simulated system UI, and no ad that could be mistaken for a Reclaim feature.

House rule that follows from this: a sponsor card never contains a `PillToggle` and never borrows feature iconography. It shares surface, radius, shadow, type, and spacing, and nothing else. The label and the missing toggle keep it honest.

#### Placement

| Surface | Slot | Notes |
|---|---|---|
| Home | One, at the bottom of the scroll | The only high-traffic slot. Never above the fold. |
| About | One, at the bottom | Low traffic, keeps a second unit available for later measurement. |
| Settings, Apps, Reels, Web Blocker, app picker | None | Decision screens stay clean. |
| Onboarding | None | Setup is never monetized. |
| Web platform view | None | The distraction-free surface stays distraction-free. |
| Widgets | None | Not possible and not wanted. |
| Block overlay | Never | An ad at the moment of interruption is coercive and would fail review. |

#### Frequency policy

Pure function, unit-tested:

```
fun AdFrequencyPolicy.canShow(
  now: Instant,
  onboardingComplete: Boolean,
  impressionsThisSession: Int,
  impressionsToday: Int,
  lastShownAt: Instant?,
  hasFill: Boolean
): Boolean
```

Rules:

- Onboarding must be complete.
- At most one impression per app session.
- At most three impressions per local day.
- At least twenty minutes between impressions.
- No fill or offline means no card, and the slot collapses to zero height with no placeholder and no layout shift.
- No refresh timer and no preload pool. One ad is requested when the slot becomes eligible and its space is measured.

#### Rendering

- `res/layout/native_ad_card.xml` hosts a `NativeAdView`, because AdMob needs real `View` references for headline, body, icon, media, CTA, and AdChoices.
- Compose wraps it in `AndroidView` inside a `SoftCard` whose radius, elevation, and padding come from the design tokens.
- The media area is a rounded 16:9 band backed by `MediaView` so video creatives render legally, started muted and never autoplaying with sound.
- The CTA uses the secondary `PillButton`. The primary black pill stays reserved for Reclaim actions.
- Colors, type, and spacing are read from the tokens at inflate time, not hardcoded in XML.
- Long headlines and large font scales truncate at two lines instead of growing the card.

#### SDK lifecycle

- `MobileAds.initialize` runs only after the UMP result is known and only when a slot could actually show.
- `RequestConfiguration`: maximum content rating `G`, no custom targeting, no content URL, no child-directed flag, no location override.
- One cached `NativeAd`, destroyed on reuse or after one hour.
- Never request an ad while the block overlay is visible or a permission setup flow is running.

#### Disclosure

- The onboarding summary says it once, in one sentence: "Reclaim is free. A single sponsor card on Home helps cover its costs."
- Settings shows one line under Privacy saying that ads are served by Google and that personalization follows the consent choice.
- The store listing declares that the app contains ads.

#### Revenue reality

- Firebase Analytics and Crashlytics are free at any scale this app will reach; the GA4 limits sit far above our event volume.
- Real costs are the one-time Play Console fee, an optional domain, and developer time. Ads do not need to exist to keep the app alive; they are upside, not survival.
- AdMob pays out at a 100 USD threshold, so early revenue can sit in the account for a while.
- Rule: never add a slot, raise caps, or move an ad into a decision screen to chase revenue without checking whether it changes retention.

#### QA

- Debug and internal builds use Google test ad unit IDs only.
- Production unit IDs are injected by CI and never committed.
- Never tap a live ad on your own device.
- Verify the no-fill, offline, capped, and consent-denied paths on the Redmi before every release.

## 5. Premium visual system

### 5.1 Art direction

The app should feel like a calm, warm, premium iOS product that happens to run on Android.

The reference image is the north star for mood, not for assets or branding.

Core adjectives: soft, tactile, friendly, airy, refined, quietly playful.

Rules:

- Off-white canvas everywhere; no pure black screens and no dark surfaces in v1.
- Floating white cards with diffuse, warm shadows instead of borders.
- Generous corner radii and breathing room.
- Restrained pastel accents used for identity and data, never as large saturated blocks.
- Small, high-quality data visualizations rather than dense dashboards.
- A consistent icon and illustration voice across every screen.
- One clear primary action per screen.
- Motion is gentle and purposeful; nothing bounces or flashes for attention.

Avoid:

- Harsh 1 dp gray borders as the main separator.
- Heavy drop shadows or hard black shadows.
- Excessive gradients or multiple competing gradients per screen.
- Generic dashboard grids, dense KPI tiles, and table-like layouts.
- Flat, sterile minimalism with no personality.
- Neon, pure white on pure black, or saturated primary colors.
- Decorative elements that compete with the primary action.

### 5.2 Color tokens

Canvas and surfaces:

| Token | Value | Use |
|---|---|---|
| `canvas` | `#F6F4EF` | App background |
| `canvasRaised` | `#EFECE4` | Grouped background, sheet headers |
| `surface` | `#FFFFFF` | Cards, sheets, dock |
| `surfaceMuted` | `#FBFAF7` | Inner tiles, input backgrounds |
| `hairline` | `#ECE7DD` at 60 percent | Only when a visual separator is truly needed |
| `ink` | `#1F1B16` | Primary text |
| `inkSecondary` | `#6F675D` | Secondary text |
| `inkTertiary` | `#9A9186` | Captions, disabled |
| `shadow` | `#3A3226` at 8 percent | Card and dock shadow |

Pastel accents:

| Token | Soft | Deep (for text and icons) |
|---|---|---|
| `sage` | `#DDEBDD` | `#4E7A5A` |
| `peach` | `#F8E0D2` | `#B4643F` |
| `butter` | `#F7ECCB` | `#8A6A26` |
| `sky` | `#DCE9F7` | `#3E6E9E` |
| `lilac` | `#E6DFF4` | `#6B5A93` |
| `blush` | `#F6DDE3` | `#A3536A` |

Backdrops:

The section color is the page, not the card. Each tab paints a soft wash behind the content that falls in from the top when the tab changes and fades back to the canvas before the bottom of the screen. Cards stay white and crisp on top of it.

- Home: sage. Apps: butter into peach. Reels: lilac. Web: sky.
- The wash covers the top third and is fully transparent by 78 percent of the height, so content always sits on calm space.
- Never more than one wash on screen, and never a wash behind a card's body text.

Glass:

The dock is the one translucent surface in the app, at 74 percent white, and it is the only place transparency is allowed. A 168 dp canvas scrim sits behind it so content dissolves before it slides under the bar, which keeps the icons readable while shapes still show through.

Control color:

Active controls inherit the tab's accent, so nothing stays green on a lilac page. A switch that is on, a selected check, a filled day chip, the selected segment label, and the active dock icon all use the section's deep tone. Neutral chrome (track recesses, secondary buttons, the dock capsule) stays neutral so the accent still means "on".

Card gradients:

- `sunrise`: peach, blush, lilac, and sky at soft stops, used for onboarding heroes and celebratory cards.
- `sageMist`: canvas to sage, used for the Protection hero card and success states.
- `dawn`: butter to peach, used for downtime and evening themes.
- There are no card gradients. Depth comes from the white surface and its shadow, and color comes from the page wash and the pastel icon circles.

Sentiment mapping:

- Apps section: peach.
- Reels section: lilac.
- Web section: sky.
- Downtime and evenings: butter and dawn.
- Success and protection: sage.
- Destructive actions: blush with deep ink, never full red.

### 5.3 Typography

- Family: PT Serif, bundled, SIL OFL.
- Weights: 400 and 700; the scale maps 500 to regular and 600 to bold.
- Display: 32-34 sp, line height 38-40, weight 700, tracking -0.5.
- Onboarding hero: 36 sp, line height 44, weight 400.
- Screen title: 28 sp, line height 34, weight 700.
- Section title: 20 sp, line height 26, weight 700.
- Card title: 17 sp, line height 22, weight 600.
- Body: 15-16 sp, line height 23, weight 400-500.
- Caption: 12-13 sp, line height 17, weight 500.
- Numbers: tabular figures, weight 700, tight tracking.
- No all-caps labels except tiny overline text with 0.4 tracking.
- Copy is sentence case, warm, and concise.

### 5.4 Spacing, shape, and elevation

- Spacing scale: 4, 8, 12, 16, 20, 24, 32, 40.
- Screen horizontal padding: 20 dp.
- Card padding: 20 dp.
- Gap between cards: 14 dp.
- Section gap: 28 dp.
- Card radius: 28 dp.
- Inner tile radius: 20 dp.
- Input radius: 18 dp.
- Pills and buttons: fully rounded.
- Button height: 54 dp; compact pill height: 40 dp.
- Touch target: minimum 48 dp.
- Card shadow: y offset 8 dp, blur 24 dp, spread 0, color `shadow`.
- Pressed state: scale 0.98, shadow reduced, no color flash.
- Only the dock, hero cards, and modals get stronger elevation.

### 5.5 Components

Every screen composes these primitives:

| Component | Description |
|---|---|
| `SoftCard` | White rounded surface with a small diffuse shadow |
| `PillButton` | Primary black pill, secondary white pill, and pastel pill variants |
| `PillToggle` | Inset track with a raised thumb; on-state takes the section accent; 180 ms, no bounce |
| `SegmentedPills` | Two to three options in the shared segmented track, with the section accent on the selected label |
| `StatusRing` | Circular progress with 8 dp rounded stroke and animated fill |
| `MiniBarChart` | 7 rounded bars, no axes, no gridlines, soft pastel fills |
| `CircularCheck` | Selectable circular check that fills with the section accent |
| `IconBubble` | 36-40 dp pastel circle holding a 20-24 dp solid icon |
| `FloatingDock` | The same segmented track with 4 icon cells, in translucent glass; tap or drag across it to change tabs |
| `PermissionCard` | Service name, benefit line, status pill, and action button |
| `StatPill` | Tiny icon plus number, used inside cards and charts |
| `ScreenScaffold` | Canvas background, safe insets, optional floating dock, standard header |

Components own their spacing and color variants so screens stay consistent.

`NativeAdCard` in the `ads` package is the one card that is not a design primitive: it composes `SoftCard`, `IconBubble`, and `PillButton` so a sponsor card inherits the visual language without handing screens a reusable ad component to scatter around.

### 5.6 Icon system

- Font Awesome Free (Solid) at 24 dp, shipped as the `fa_solid_900` font and drawn by `ReclaimIcon`.
- Icons always sit in a pastel `IconBubble` when used at screen level.
- Icons inside buttons are 18-20 dp and inherit the button text color.
- Platform glyphs are our own simple marks or letter tiles, not official logos.
- Active dock icon is solid ink with a filled pill background; inactive is inkTertiary.

### 5.7 Illustration system

There is no mascot and no character. Personality comes from type, color, and a small set of objects that describe what the app does.

Object illustrations:

- Soft 3D phone with a shield.
- Hourglass with sand.
- Cloud with a tiny lock.
- Sun and moon pair.
- Small leaf or sprout for growth.

Asset rules:

- Original generated art, no third-party character or brand.
- Transparent or off-white backgrounds, WebP, 1024 px hero and 256 px small variants.
- Keep the total illustration payload under 2 MB.
- Art direction prompts and reference notes live in `docs/art-direction.md`.
- Generated assets are reviewed for consistency of lighting, material, and palette before check-in.

Usage rules:

- One hero illustration per screen maximum.
- No decorative illustrations in dense lists.
- Illustrations never overlap text or controls.

### 5.8 Motion

- Tab change: one motion. The page glides 420 ms on an eased curve, the wash crossfades 340 ms with a five percent rise, and the dock capsule tracks the pager position continuously instead of stepping at the halfway mark. Nothing starts after the page has already landed.
- Tab change by drag: the pages follow the finger, the capsule sits under the finger, and the wash starts changing exactly when the new page becomes current.
- Insets: the toggle track and the segmented track carry a top inner shadow and a raised thumb or pill, so the controls read as recessed into the page.
- Standard enter transition: fade plus 16 dp upward slide, 260 ms, spring damping 0.85.
- Onboarding step transition: fade plus 24 dp horizontal slide.
- Card press: scale to 0.98 over 120 ms.
- Toggle: thumb and track animate over 180 ms.
- Ring and bars animate from zero on first appearance over 600 ms.
- Stagger list items by 40 ms on first render only.
- Haptics: light impact on option selection and successful permission return; no haptics on scroll.
- Respect system reduced motion: disable loops, parallax, and long transitions.

### 5.9 Screen specifications

#### Home

- Top row: large greeting, settings icon in a soft circle. No date line: the screen is about the day ahead, not the calendar.
- Hero card with a large status ring showing active protections out of three, a friendly status line, and a secondary pill.
- `Interventions` card with a 7-day mini bar chart, total count, and two stat pills for app blocks and reels breaks.
- Feature cards stacked: Apps (peach), Reels (lilac), Web (sky). Each has an icon bubble, title, status line, pill toggle, and chevron.
- Sponsors slot at the very bottom: one `NativeAdCard`, collapsed to nothing when no ad is eligible.
- Floating dock at the bottom with Home, Apps, Reels, Web.

#### Apps

- Header with title and search pill.
- `Downtime` hero card with start and end time, day chips, and a pill toggle.
- `Blocked apps` card with selected count and a list of app rows using circular checks.
- `Notifications` card with a soft toggle and permission state.
- Emergency pause as a quiet text button at the bottom, never a loud action.
- Section identity: butter wash behind the page and peach icons.

#### Reels

- Header with title and a small `Budget` info pill.
- Hero `Scroll budget` card with a status ring and the current sensitivity.
- Sensitivity segmented control with three cards, each with a tiny meter.
- Platform toggles as a list of icon bubbles with pill toggles.
- 7-day breaks mini chart.
- Chat-safe note with a small shield icon.
- Section identity: lilac and soft violet only.

#### Web Blocker

- Hero status card with a shield icon, the site count, and the filter toggle.
- Category cards with pastel icons and counts.
- Custom domains card with pill input and removable chips.
- 7-day blocked queries mini chart.
- "How it works" disclosure card with DNS explanation and limitations.
- Section identity: sky and cool neutrals only.

#### Web Platform

- Minimal top bar with back, platform name, refresh, and overflow.
- WebView fills the screen on canvas.
- Thin loading bar in deep pastel, not a spinner overlay.
- Error and offline states use a short line, a small object illustration, and a pill retry.
- Floating dock is hidden to maximize content.

#### Settings

- Grouped soft cards: Appearance, Permissions, Web data, Privacy and analytics, Diagnostics, About.
- Privacy card shows the analytics toggle, reset analytics ID, and a link to the policy.
- Privacy card also states in one line that ads are served by Google and follow the consent choice.
- No dense switches lists; each row has an icon bubble and generous spacing.

#### About

- App mark, version, one-paragraph purpose statement, and links to source, license, privacy policy, and analytics docs.
- One `NativeAdCard` at the bottom, under the same caps as Home.

#### Block overlay

- Full-screen sunrise gradient with one calm object illustration.
- White inner card with big title, one short explanation, and two pill buttons: "Go home" and "Open Reclaim".
- Optional small line showing the active rule.
- No harsh red, no countdown pressure, no shame copy.
- For downtime, use the dawn gradient and the moon object.
- For reels, use the lilac gradient and the scroll object.

#### Onboarding

- See section 6 for the full flow and visuals.

### 5.10 Visual QA

- Golden screenshot tests with Roborazzi for Home, Apps, Reels, Web, Overlay, and one onboarding step.
- Manual screenshot review on the Redmi at default and large font scale.
- Token audit: no hardcoded colors or radii outside the design package.
- Shadow audit: only `SoftCard`, dock, and modal shadows.
- Border audit: no 1 dp borders used as layout separators.
- Contrast audit: all text passes WCAG AA; pastel text uses deep tokens only.
- Touch target audit: every interactive element at least 48 dp.
- TalkBack walkthrough of Home, onboarding, and overlay.
- Reduced motion check.
- Illustration weight check in the release APK.

### 5.11 Slop audit

Every screen gets reviewed against the [unslop](https://github.com/heydamir/unslop) checklist (Damir Sokolovsky, CC BY 4.0), adapted for Compose and for this direction. The credit lives in the README.

Adopted as written: 4px spacing grid, one tinted neutral ramp, three text roles, one accent per screen, hairlines at 6 to 10 percent ink, one icon set at one size and stroke, no emoji in the interface, nested radii, short labels with no trailing period, no exclamation marks, no hype words, tabular figures, one easing curve, no looping motion, and nothing that animates layout properties.

Deliberate deviations, because the direction in 5.1 asks for them:

- Display type keeps weights 700 and 800. unslop prefers 400 to 600 with hierarchy from size alone; a soft premium app reads flat without a bold display face.
- The Home greeting stays. It is part of the product's warmth, but the line under it must state something specific about the user's real state, never a generic welcome.
- Overlines lose uppercase tracking. The date and section labels become sentence case with light tracking.
- There is no mascot and no idle animation. Illustration stays static and motion is reserved for state changes.

## 6. Conversational onboarding

### 6.1 Goals

- Feel like a short, warm conversation, not a setup checklist.
- Personalize the app before the first Home screen.
- Collect only what improves the experience, all stored locally.
- Reach the first useful state in under two minutes.
- Make permissions feel like a natural part of the conversation, not a wall of system dialogs.

### 6.2 Tone

- Friendly, calm, specific, and concise.
- No guilt, no fear, no medical claims, no "addiction" language.
- Questions are one sentence and use plain words.
- Helper text is optional and never repeats the title.
- Buttons are short and human: "Sounds good", "Not now", "Show me", "I'm ready".
- Progress is visible but not gamified with scores.

### 6.3 Flow

Every step is a full-screen page with a back chevron, progress dots, an optional object illustration, a question, optional helper text, one input pattern, and a pinned continue button.

| # | Step id | Question | Input | Personalization | Analytics |
|---|---|---|---|---|---|
| 1 | `welcome` | "Let's make your phone feel lighter." | None, CTA "Let's begin" | None | `onboarding_step_viewed` |
| 2 | `name` | "What should we call you?" | Text input, optional | Local display name | `step_completed` with code `named` or `skipped` |
| 3 | `apps` | "Which apps pull you in?" | Multi-select platform cards | Pre-select blocked apps and reels platforms | `step_completed` with selected count bucket |
| 4 | `struggle` | "Where does it hit hardest?" | Multi-select cards: endless scrolling, notifications, opening apps, websites | Chooses feature emphasis and default web categories | `step_completed` with option codes |
| 5 | `downtime` | "When should we protect your evenings?" | Three presets: evenings, late night, not now | Sets downtime and days | `step_completed` with preset code, no exact times |
| 6 | `sensitivity` | "How much scrolling feels okay?" | Three illustrated cards: Gentle, Balanced, Strict | Sets scroll budget and cooldown | `step_completed` with level |
| 7 | `app_permission` | "Let Reclaim step in when a blocked app opens." | Permission card plus system button | Enables app blocker when grant returns | `permission_prompted`, `permission_result` |
| 8 | `content_permission` | "Let Reclaim notice when you're in Reels or Shorts." | Permission card plus system button | Enables content blocker when grant returns | Same |
| 9 | `web_setup` | "Want to quiet the mobile web too?" | Category chips plus VPN consent card | Sets categories and starts VPN if allowed | `permission_prompted`, `permission_result`, `vpn_started` |
| 10 | `notifications` | "Should blocked apps stay quiet?" | Soft explainer plus enable button, skippable | Enables notification suppression | `permission_prompted`, `permission_result` |
| 11 | `analytics` | "Help us make Reclaim better?" | Toggle with short explanation, default off | Sets analytics consent | `consent_analytics_set(enabled)` |
| 12 | `summary` | "Your calm plan is ready." | Summary card with choices, one funding line, CTA "Start using Reclaim" | Writes onboarding complete, opens Home | `onboarding_completed(duration_sec)` |

Adaptive rules:

- Steps 7 and 8 are skipped when step 3 selected no relevant apps.
- Step 9 is skipped when step 4 did not include websites.
- Step 10 is optional and always skippable.
- Step 11 is always shown but never blocking; "Not now" is a first-class action.
- The flow has a visible "Skip setup" action after step 2 that jumps to the summary with sensible defaults.

### 6.4 Copy examples

- Step 1 title: "Let's make your phone feel lighter."
- Step 1 helper: "A few quick questions, then Reclaim handles the rest."
- Step 3 title: "Which apps pull you in?"
- Step 3 helper: "Pick as many as you like. You can change this later."
- Step 5 title: "When should we protect your evenings?"
- Step 5 helper: "Reclaim can step in when you'd rather be offline."
- Step 6 title: "How much scrolling feels okay?"
- Step 6 helper: "Gentle gives you more room. Strict steps in sooner."
- Step 7 title: "Let Reclaim step in when a blocked app opens."
- Step 7 helper: "Android needs accessibility access for this. Reclaim only checks which app is on screen."
- Step 11 title: "Help us make Reclaim better?"
- Step 11 helper: "Share anonymous usage events and crash reports. Never your apps, sites, name, or content."
- Step 12 title: "Your calm plan is ready."
- Step 12 helper: "You can change everything later in Settings."
- Step 12 also carries one funding line: "Reclaim is free. A single sponsor card on Home helps cover its costs."

### 6.5 Visual layout

- Off-white canvas with a soft sunrise glow at the top, fading before the content.
- Progress dots at the top, 6 dp each, active dot stretches into a 22 dp pill.
- Object illustration centered above the question, 120-160 dp tall.
- Question in display typography, maximum two lines.
- Helper text in secondary ink, maximum two lines.
- Options are full-width white cards with icon bubbles, title, and a circular check.
- Selected cards get a pastel tint and a deeper pastel ring, never a hard border.
- Continue button is a pinned black pill above the system navigation area.
- Secondary actions are text buttons in secondary ink.
- No page scroll unless the content genuinely needs it.
- Every step fits on a 6.1-inch screen without scrolling.

### 6.6 Personalization mapping

- Step 3 apps become the initial blocked package set and enabled reels platforms.
- Step 4 choices choose which feature cards are emphasized on Home and which permissions are requested.
- Step 5 becomes the downtime schedule and enables the downtime card.
- Step 6 sets `content_sensitivity` and cooldown.
- Step 9 sets web categories.
- The summary shows a human-readable plan, for example "4 apps blocked, evenings protected, balanced scroll budget, social sites filtered".

### 6.7 Analytics for onboarding

- Log step views and completions by `step_id` and `step_index`.
- Log option codes, never free text.
- Log duration and skip points.
- Log permission prompts and results, not the underlying app selections.
- If analytics consent is off, queue nothing and send nothing; local onboarding state still works.
- Step 11 must set consent before any post-onboarding events are sent; events from steps 1-10 are buffered only if consent was already on from a previous install.

### 6.8 Edge cases

- User leaves mid-onboarding and returns: resume at the last incomplete step.
- Permission denied: show a gentle recovery card with "Try again" and "Continue without it".
- VPN unavailable because another VPN is active: explain and allow continuing.
- Accessibility service blocked by restricted settings: deep link to app info and instructions.
- Large font scale: steps must scroll rather than clip.
- Reduced motion: no step slide beyond a fade.

## 7. Analytics and measurement

### 7.1 Stack decision

Use Firebase Analytics (GA4) plus Firebase Crashlytics.

Reasons:

- Free at the scale of an early open-source app.
- Native Play integration and DebugView for QA.
- Built-in retention, funnel, and audience reporting without a backend.
- Crashlytics gives crash-free rates and ANR context for Play launch.

Alternatives if Firebase is rejected:

- Aptabase or PostHog for privacy-first product analytics.
- Sentry for crash reporting.

The implementation stays behind `AnalyticsTracker` and `CrashReporter` interfaces so the provider can change without touching feature code.

### 7.2 Consent model

- Analytics and crash reporting are off by default.
- Onboarding step 11 is the first ask, with clear value exchange and a real "Not now".
- Settings contains the same toggle plus a "Reset analytics ID" action.
- When off: `setAnalyticsCollectionEnabled(false)`, Crashlytics collection disabled, no events buffered for later.
- When turned off later: call `resetAnalyticsData()` and stop collection immediately.
- The consent choice is stored locally and never transmitted as part of an event.
- Ads use Google's UMP form, a separate question from the analytics toggle: analytics asks "may we measure?", UMP asks "may we personalize ads?".
- If UMP reports no consent or an unknown region, AdMob requests non-personalized ads only.
- Declining either consent never blocks the app; every feature works regardless.
- The privacy policy explains what is collected, why, retention, and how to reset or request deletion.

### 7.3 Event taxonomy

Naming rules:

- `snake_case`, 40 characters maximum.
- One event name per user intent, not per UI control.
- Parameters are enums, booleans, or bucketed numbers.
- No free text, names, package names, domains, URLs, page titles, or notification content.
- Events fire from ViewModels or repositories, not from Compose recomposition.

Core events:

| Event | When | Parameters |
|---|---|---|
| `screen_view` | Navigation destination changes | `screen_name` |
| `onboarding_started` | First onboarding screen shown | none |
| `onboarding_step_viewed` | Step becomes visible | `step_id`, `step_index` |
| `onboarding_step_completed` | User advances | `step_id`, `option_code`, `duration_ms` |
| `onboarding_skipped` | User skips setup | `step_id` |
| `onboarding_completed` | Summary CTA tapped | `duration_sec` |
| `permission_prompted` | System permission UI opened | `permission` |
| `permission_result` | App returns from system UI | `permission`, `granted` |
| `feature_toggled` | Any master feature switch | `feature`, `enabled` |
| `apps_selected_bucket` | Blocked app selection changes | `bucket` |
| `downtime_set` | Schedule saved | `days_count`, `has_overnight`, `enabled` |
| `reels_platform_toggled` | Platform switch | `platform`, `enabled` |
| `reels_sensitivity_set` | Sensitivity saved | `level` |
| `block_overlay_shown` | Overlay presented | `kind`, `reason` |
| `reels_break_shown` | Content overlay presented | `platform`, `sensitivity`, `scroll_bucket` |
| `web_category_toggled` | Category switch | `category`, `enabled` |
| `web_custom_domains_bucket` | Custom list changes | `bucket` |
| `vpn_started` | VPN established | `category_count` |
| `vpn_revoked` | System revokes VPN | none |
| `widget_added` | Widget first update | `type` |
| `widget_tapped` | Widget tap | `type`, `platform` |
| `web_platform_opened` | Web screen opened | `platform` |
| `web_script_failed` | Injection throws | `platform`, `reason_code` |
| `daily_stats` | Once per day when consent is on | `reels_breaks`, `app_blocks`, `dns_blocks`, `active_services` |

Ad impressions and clicks are not duplicated as Firebase events. AdMob reporting is the source of truth, and local caps are enforced by `AdFrequencyPolicy`.

Prohibited parameters:

- Display name or any free text.
- Package names or app labels of blocked apps.
- Custom domains or queried domains.
- Page URLs, page titles, or WebView content.
- Notification titles or text.
- Precise location, contacts, photos, or files.
- Advertising ID in any Firebase event. The AdMob SDK uses it under UMP consent; our event layer never touches it.
- Ad content, advertiser names, impressions, clicks, or revenue.

### 7.4 User properties

- `onboarding_complete`: boolean.
- `primary_goal`: enum code from step 4.
- `sensitivity`: enum code.
- `reels_platform_count`: 0, 1-2, 3+.
- `web_category_count`: 0, 1-2, 3.
- `app_blocking_enabled`, `vpn_enabled`, `notifications_enabled`: booleans.
- `theme`: light in v1.
- User properties are set from settings changes and never include names or package lists.

### 7.5 Local counters and daily aggregate

In-app charts must reflect real local activity, not analytics data.

`CounterRepository` stores daily buckets for:

- App block overlays shown.
- Reels breaks shown.
- DNS queries blocked by category.
- Active protection days.

The Home and feature screens read these counters to render the mini charts.

Once per day, when consent is on, `DailyStatsWorker` sends one aggregate `daily_stats` event and records `last_stats_upload_day`.

The worker runs with WorkManager under a network constraint and a unique name, so it never floods analytics.

### 7.6 KPIs

Activation:

- Onboarding completion rate.
- Median time to complete onboarding.
- Permission grant rate per permission.
- First protection enabled within 24 hours.

Adoption:

- App blocking enabled.
- Reels blocking enabled per platform.
- VPN enabled and category adoption.
- Widget added.
- Web platform opened.

Retention:

- D1, D7, D30 retention.
- Active protection days per user.

Quality:

- Crash-free users.
- ANR rate.
- Overlay shown per active day, to detect false positives.
- VPN start failure rate.

### 7.7 Crash reporting

- Crashlytics collects stack traces, app version, OS version, device model, and feature flags.
- Custom keys are limited to enum-like states such as `active_feature`, `service_state`, and `vpn_state`.
- No names, package names, domains, URLs, or content.
- Non-fatal reports are used for service start failures, VPN setup failures, and injection failures.
- Mapping files upload only in release CI.
- Crashlytics collection follows the analytics consent toggle.

### 7.8 Analytics QA

- Unit test the fake tracker records the exact event names and parameter sets.
- Lint-style test that no prohibited keys appear in `AnalyticsEvents`.
- Verify no event fires when consent is off.
- Verify consent changes take effect immediately.
- Walk the onboarding and every feature on a debug build with Firebase DebugView.
- Confirm DailyStatsWorker sends exactly one event per day.
- Confirm Play Data Safety matches the implemented data types.

### 7.9 Privacy and Data Safety

Data collected when consent is on:

| Data type | Examples | Purpose |
|---|---|---|
| App activity | screens viewed, features toggled, onboarding steps, coarse usage counts | Analytics |
| App info and performance | crash logs, ANR traces, diagnostics | Analytics and app functionality |
| Device or other IDs | Firebase installation ID, app instance ID | Analytics |
| Device or other IDs (ads) | Advertising ID, ad request and interaction data | Advertising, `play` flavor only |
| Approximate location (ads) | Coarse, IP-derived, from the ad request | Advertising, `play` flavor only |

Not collected:

- Name, email, phone, contacts, photos, files, precise location.
- Blocked app package names, custom domains, URLs, page content, notification content.
- Advertising ID or ad data inside any Firebase event.
- Data sold to third parties.

Data Safety form:

- Data is optional because analytics requires opt-in.
- Data is encrypted in transit.
- Users can request deletion by email and can reset the analytics ID in Settings.
- Retention: GA4 property set to 2 months for events and 14 months for user data, or the shortest available that still supports retention analysis; Crashlytics 90 days.
- Ads data is collected and used by Google AdMob as a third party for advertising; this is declared as sharing with Google.
- Analytics and crash data are optional because they need opt-in. Ads data is not optional because the app is free and ad-funded, but personalization is optional through UMP.
- Deletion paths: reset the analytics ID in Settings, reset the system advertising ID, or email for the rest.

### 7.10 Counting users

- Active users, new users, sessions, retention, and the per-version and per-country breakdowns come from Firebase Analytics. GA4 counts a user as an app instance that opened the app and sent at least one event.
- Because analytics is opt-in, that number is a floor, not a headcount. A user who declines consent is invisible by design.
- Absolute installs, uninstalls, and store conversion come from Play Console, which is not consent gated.
- Read GA4 for behaviour and trends, Play Console for install totals, and never present the GA4 figure as "total users".
- No user ID is sent, so an app instance is the unit. A reinstall counts as a new user, and one person on two devices counts as two.

## 8. Google Play launch

### 8.1 Policy declarations

| Area | What Play requires | Our approach |
|---|---|---|
| Accessibility API | Declaration, core-function justification, video demo, disclosure | Both services are core to app blocking and reels blocking; show in-app disclosure and onboarding explanation |
| VPNService | Declaration, prominent disclosure, no traffic redirection | DNS filtering is the core function; show a dedicated disclosure before consent; never inspect or redirect payload traffic |
| Foreground service | Type declaration and in-app disclosure | `specialUse` with subtype `dns_filter`; persistent notification explains why |
| Data safety | Accurate data types, purposes, optionality, deletion | Matches section 7.9 exactly |
| Ads | Accurate "contains ads" declaration; no disruptive, deceptive, or self-clicking ads | Declare contains ads. Native sponsor cards only, labeled, capped, never full-screen, never on the block overlay |
| Advertising ID | `AD_ID` permission use declared | Manifest permission in the `play` flavor only; no ad request before UMP resolves |
| Target audience | 13+ | Not a children's app, no Families policy |
| App access | All functionality without login | True, no accounts |
| News, health, finance, government | Not applicable | Declare accordingly |
| Privacy policy | Public URL | Host in the repo via GitHub Pages |
| Account deletion | Required if accounts exist | No accounts; analytics reset and email deletion path documented |

### 8.2 Store listing assets

- App icon 512 x 512, adaptive icon, a soft abstract mark from the sprout glyph.
- Feature graphic 1024 x 500 with a calm object illustration and a short message.
- 8 phone screenshots at 1080 x 1920 showing onboarding, Home, Apps, Reels, Web, overlay, widgets, settings.
- Short description under 80 characters, for example "A calmer phone. Block the scroll, keep the good parts."
- Full description with features, permissions rationale, privacy statement, and limitations.
- No SocialLite name, screenshots, assets, or trademarks.
- No medical or addiction claims.
- Screenshots use the real app UI, not mockups that misrepresent behavior.
- One screenshot includes the sponsor card so the listing matches what users actually see.

### 8.3 Release process

1. Create the Play Console app with package `dev.reclaim.app`.
2. Enable Play App Signing.
3. Build a release AAB with the production Firebase config supplied by CI.
4. Run internal testing with at least the developer account and a second device.
5. Complete all policy declarations and Data Safety.
6. Run the Play pre-launch report and fix all crashes, ANRs, and accessibility warnings.
7. Start a closed test if the account requires it, with at least 12 testers for 14 days.
8. Promote to production with a staged rollout: 10 percent, 50 percent, 100 percent.
9. Monitor crash-free rate, ANR rate, and activation metrics between stages.
10. Keep a GitHub Release APK as a fallback distribution channel.
11. Before requesting production ads, publish `app-ads.txt` at the developer website root and link that website in the Play listing.

### 8.4 Post-launch monitoring

- Daily check of Crashlytics and ANR clusters for the first two weeks.
- Weekly funnel review: onboarding completion, permission grants, first protection, retention.
- Alert on any event parameter that unexpectedly contains free text.
- Review accessibility and VPN policy emails immediately.
- Ship detection fixes for social apps quickly; view IDs change often.

## 9. Permissions and onboarding

| Permission or service | Needed for | Request path |
|---|---|---|
| Accessibility service (App Blocker) | Foreground app detection | Onboarding step 7, system settings deep link |
| Accessibility service (Content Blocker) | Reels/Shorts detection | Onboarding step 8 |
| `BIND_VPN_SERVICE` via `VpnService.prepare` | DNS filtering | Onboarding step 9 and Web Blocker screen |
| Notification listener | Suppressing notifications from blocked apps | Onboarding step 10, optional |
| `POST_NOTIFICATIONS` | VPN foreground notification | Runtime prompt on Android 13+ |
| `RECEIVE_BOOT_COMPLETED` | Restore notification listener and re-evaluate state | Manifest only, no prompt |
| `INTERNET` | WebView, Firebase, and ad requests | Manifest only |
| `AD_ID` | Ad attribution, `play` flavor only | Manifest only, declared in Data Safety |
| `ACCESS_NETWORK_STATE` | Upstream DNS availability | Manifest only |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` | VPN lifecycle | Manifest only |
| `SYSTEM_ALERT_WINDOW` | Fallback overlay only if activity launch is refused | Settings diagnostics, explicit opt-in |

No usage-stats permission, no package-query permission, no location, camera, microphone, contacts, or storage.

Onboarding must detect current state and never claim a service is on when it is off.

## 10. Source tree and assets

```
reclaim/
  .github/workflows/ci.yml
  .github/workflows/release.yml
  .gitignore
  LICENSE
  README.md
  CONTRIBUTING.md
  docs/PRIVACY.md
  docs/art-direction.md
  docs/analytics-events.md
  docs/play-declarations.md
  docs/monetization.md
  docs/app-ads.txt
  gradle/libs.versions.toml
  gradlew, gradle/wrapper/
  settings.gradle.kts
  build.gradle.kts
  app/
    build.gradle.kts
    proguard-rules.pro
    google-services.json.example
    src/main/AndroidManifest.xml
    src/main/java/dev/reclaim/app/...        (as in 3.2)
    src/main/res/
      layout/widget_platform.xml
      layout/widget_quick_launch.xml
      layout/native_ad_card.xml
      xml/app_blocker_config.xml
      xml/content_blocker_config.xml
      xml/widget_info_*.xml
      values/strings.xml, themes.xml, colors.xml
      drawable/ (own vector glyphs)
      font/ (PT Serif files)
    src/main/assets/
      domains/social.txt, adult.txt, gambling.txt
      illustrations/obj_*.webp
      web/base.css
      web/instagram.js, youtube.js, x.js, reddit.js, facebook.js, tiktok.js, snapchat.js, linkedin.js
    src/test/java/dev/reclaim/app/...       (unit tests)
    src/androidTest/java/dev/reclaim/app/... (instrumentation)
```

No asset, icon, string, illustration, or script is copied from SocialLite or the reference app.

The `libre` flavor adds no source sets. It swaps `AppContainer` bindings for NoOp implementations and drops the Firebase, AdMob, and UMP dependencies, so the same code compiles into a build with no proprietary SDKs.

## 11. Phased execution plan

Each phase ends with a working build, passing checks, and a manual verification note.

Nothing is installed on the machine without asking first.

### Phase 0: Environment, Firebase, and repository skeleton

Tasks:

1. Check the Android SDK for platform 36, build-tools, and platform-tools.
2. Confirm the JDK required by AGP; ask before installing anything.
3. Create the Firebase project and register `dev.reclaim.app` with your Google account; enable Analytics and Crashlytics. Ask before this step, since it touches your account.
4. Initialize git in `/Users/sher/Development/reclaim`.
5. Add `.gitignore`, `LICENSE` (pending D3), `README.md`, and `CONTRIBUTING.md`; commit `google-services.json.example`, never the real file.
6. Create the Gradle wrapper and version catalog.
7. Create the app module with `compileSdk 36`, `targetSdk 36`, `minSdk 29`, application ID `dev.reclaim.app`.
8. Add the `play` and `libre` flavors, with `google-services` and the ad dependency applied per flavor so a clean checkout builds without config.
9. Add Compose, Material 3, Navigation, DataStore, Lifecycle, Firebase BoM, Crashlytics, and test dependencies.
10. Add `AnalyticsTracker`, `CrashReporter`, `FirebaseAnalyticsTracker`, `FirebaseCrashReporter`, and NoOp implementations, plus a debug-only consent override so DebugView works before the Phase 3 consent screen exists.
11. Send one test event and one test crash from the Redmi; confirm both land in DebugView and Crashlytics.
12. Add the design token files, theme skeleton, and font.
13. Add ktlint, Android Lint, and CI for build, tests, lint, and ktlint on both flavors.

Acceptance criteria:

- `assemblePlayDebug` and `assembleLibreDebug` both succeed, including a checkout with no `google-services.json`.
- `./gradlew testPlayDebugUnitTest testLibreDebugUnitTest lintDebug` succeeds.
- A test event is visible in DebugView within 30 seconds of opening the app.
- A test crash appears in Crashlytics.
- `libreDebug` carries no Firebase or AdMob dependency on its runtime classpath.
- Release builds default to analytics collection off until consent is granted.
- App installs and shows a Reclaim-branded placeholder Home screen on the Redmi.

### Phase 1: Domain model, settings, counters, and event constants

Tasks:

1. Implement `Platform`, `PlatformRegistry`, and platform metadata.
2. Implement `AppSettings` and the DataStore-backed `SettingsRepository`.
3. Implement `DowntimeSchedule` with overnight handling.
4. Implement `BlockEvaluator`.
5. Implement `ScrollAccumulator`.
6. Implement `DomainMatcher`.
7. Implement `CounterRepository` with daily buckets, pruning, and aggregation.
8. Add `AnalyticsEvents` constants and a fake tracker for tests.
9. Write unit tests for schedule math, evaluator, accumulator, matcher, counters, and event schemas.

Acceptance criteria:

- Unit tests cover all-day, overnight, cross-day, paused, and boundary cases.
- Matcher handles `www.`, subdomains, case, trailing dot, IPs, and invalid input.
- No event constant accepts free text.
- No Android service code is written yet.

### Phase 2: Design system and app shell

Tasks:

1. Build the color, type, shape, spacing, and elevation tokens.
2. Build `SoftCard`, `PillButton`, `PillToggle`, `SegmentedPills`, `StatusRing`, `MiniBarChart`, `CircularCheck`, `IconBubble`, `FloatingDock`, `PermissionCard`, and `ScreenScaffold`.
3. Add the Font Awesome solid set and the platform glyph set.
4. Build the four-tab pager, the draggable dock, and the screens.
5. Add the offline placeholder for illustrations so layout work can proceed before art is final.
6. Add Roborazzi screenshot tests for five components.
7. Audit hardcoded styles and remove them.

Acceptance criteria:

- The component gallery renders on the Redmi with the target mood.
- No screen uses raw Material buttons or default shadows.
- Goldens pass in CI.
- Visual QA notes are recorded.

### Phase 3: Conversational onboarding and permissions

Implementation notes:

- The two accessibility services and the notification listener are registered in this phase as stubs. Registering them is what makes the permission rows real in system settings; the blocking logic lands in Phase 4 and 5.
- Downtime offers three presets in onboarding. A custom window arrives with the Schedule screen.
- The web step records categories but does not start the VPN, because the service does not exist until Phase 6. The Web screen owns the consent flow.
- A small Settings screen (analytics toggle, run setup again, version) lands here so the Home gear is not a dead control and the summary's promise is true.

Tasks:

1. Build the 12-step onboarding flow with adaptive branching.
2. Build the question layouts: text input, multi-select cards, schedule card, sensitivity cards, permission cards, consent step, summary.
3. Persist answers and resume at the last incomplete step.
4. Wire permission detection and recovery states.
5. Add the onboarding analytics events.
6. Add the progress dots, object illustration layout, and step transitions.
7. Write Compose tests for branching, skip, and resume.

Acceptance criteria:

- A new user can finish in under two minutes.
- Skipping works at every step.
- Denied permissions show a gentle recovery card.
- The summary matches the chosen settings.
- Onboarding events appear in a fake tracker with no free text.

### Phase 4: App blocker service and overlay

Implementation notes:

- The overlay is launched directly by the accessibility service. If a device refuses background activity starts, the fallback is a `SYSTEM_ALERT_WINDOW` overlay with an explicit permission request, built only if a device needs it.
- The Home switches now persist to settings. App blocking and downtime are enforced in this phase; the reels switch waits for Phase 5 and the web switch for Phase 6.
- Notification suppression only cancels a matching notification. It never reads, rewrites, or forwards one.
- Package visibility is declared with intent queries for the launcher and for launcher activities, so no `QUERY_ALL_PACKAGES` is needed.
- MIUI freezes background app processes. While frozen, the accessibility service stays "bound" but receives no events, so blocking stops silently. Settings carries a "Stay awake" card that reports the battery exemption and links to the system list. Verified on the Garnet: frozen means no events, exempt means events resume.
- A debug-only broadcast (`DebugSettingsReceiver`) forces a downtime window or a pause, so rule variants can be tested without changing the clock.

Tasks:

1. Add `AppBlockerService` with manifest registration and `app_blocker_config.xml`.
2. Implement foreground package tracking, ignored packages, and rule evaluation.
3. Implement the 15-second schedule tick.
4. Build the premium `BlockOverlayActivity` with reason variants.
5. Implement launch cooldown, duplicate-overlay prevention, and go-home behavior.
6. Implement emergency pause in Settings.
7. Implement optional `NotificationFilterService`.
8. Add app blocker analytics and local counters.
9. Manual device test: always block, downtime block, pause, notification suppression.

Acceptance criteria:

- A blocked app never reaches the foreground for more than 1.5 seconds.
- The overlay looks correct for always, downtime, and reels reasons.
- Settings remains reachable.
- The user can disable the service at any time.

### Phase 5: Reels/Shorts content blocker

Tasks:

1. Add `ContentBlockerService` with manifest registration and `content_blocker_config.xml`.
2. Implement the detector interface and event routing by package.
3. Implement Instagram and YouTube view-ID detectors.
4. Implement TikTok scroll heuristic and beta Facebook/Snapchat heuristics.
5. Implement chat-safety marker checks.
6. Connect `ScrollAccumulator` to overlay launches.
7. Add per-platform toggles, sensitivity cards, and cooldown to the Reels screen.
8. Add the debug detection card.
9. Add analytics and local counters.
10. Manual test matrix: IG Reels, IG DMs, YT Shorts, YT normal videos, TikTok FYP, TikTok DMs, FB Reels, Snapchat Spotlight.

Acceptance criteria:

- IG Reels, YT Shorts, and TikTok FYP trigger an overlay after the configured budget.
- DMs, comments, and normal feed scrolling do not trigger.
- Leaving the app resets the session.
- Disabling a platform toggle stops detection immediately.

### Phase 6: Web blocker VPN

Tasks:

1. Implement `DnsMessage` parsing and response building with unit tests.
2. Implement `DomainMatcher` integration and bundled domain assets.
3. Implement `WebBlockerVpnService` with tun setup, read loop, protected forwarding, and NXDOMAIN replies.
4. Implement the foreground notification, stop action, `onRevoke`, and state reporting.
5. Build the Web Blocker screen with the sky hero, categories, custom domains, and charts.
6. Exclude Reclaim from the VPN.
7. Add local counters and analytics.
8. Add a debug DNS test action.
9. Manual test: enable, blocked domains fail, allowed sites load, Reclaim web works, revoke, restart.

Acceptance criteria:

- `instagram.com` fails to resolve while Social category is on.
- `example.com` resolves normally.
- Custom domains block only themselves and their subdomains.
- VPN survives screen off and stops cleanly.
- No traffic loop and no DNS leak observed in device testing.

### Phase 7: Widgets and deep links

Tasks:

1. Implement deep link parsing and navigation.
2. Build premium widget layouts with soft cards and pastel icon circles.
3. Implement `PlatformWidgetProvider` and `QuickLaunchWidgetProvider`.
4. Add widget info XML for all 8 widgets.
5. Add immutable PendingIntents and unique request codes.
6. Add own vector glyphs for each platform.
7. Add widget analytics.
8. Manual test: add each widget, tap each slot.

Acceptance criteria:

- All widgets appear in the launcher picker.
- Tapping any slot opens the correct platform.
- Widgets survive reboot and app update.

### Phase 8: Web experience and JS customizations

Tasks:

1. Build `WebPlatformScreen` and `WebViewHost` with security flags.
2. Implement host allowlisting, auth hosts, and external-link handling.
3. Implement back, reload, open in browser, and clear data.
4. Implement offline and error states with object illustrations.
5. Write our own `base.css` and per-platform scripts.
6. Inject on page finished and same-document navigation.
7. Handle login flows and the fallback message.
8. Add URL policy tests and platform key validation tests.
9. Add web analytics.
10. Manual test on each platform.

Acceptance criteria:

- No navigation outside the allowlist loads inside Reclaim.
- Instagram Reels and YouTube Shorts entries are hidden in the web experience.
- Scripts are idempotent across SPA navigation.
- Login state persists between app launches where the provider allows it.

### Phase 9: Monetization, visual polish, and Play release

Tasks:

1. Create the AdMob account, register `dev.reclaim.app`, and create the two native ad units for Home and About. Ask before touching your account.
2. Implement `AdProvider`, `AdSlot`, `NoAdProvider`, `AdMobAdProvider`, `AdFrequencyPolicy`, and `NativeAdCard` with `native_ad_card.xml`.
3. Implement `AdConsentManager` with the UMP form, the non-personalized fallback, and a debug reset action.
4. Add the Home and About slots, with collapse-on-empty and no layout shift.
5. Verify the caps, no-fill, offline, consent-denied, and day-rollover paths on the Redmi, using test ad units only.
6. Swap to production ad unit IDs through CI configuration; keep them out of the repo.
7. Confirm consent gating and reset analytics ID in Settings, add screen tracking through the NavController, and add `DailyStatsWorker` with a network constraint.
8. Verify every event and every trigger in DebugView, and add Crashlytics mapping upload in release CI.
9. Generate and polish the object illustration set.
10. Run the visual QA checklist and fix every deviation.
11. Complete Play declarations including ads, Data Safety, the privacy policy, and the store assets, and publish `app-ads.txt`.
12. Run the pre-launch report and internal testing on both flavors.
13. Tag `v1.0.0` and start a staged rollout.

Acceptance criteria:

- No sponsor card appears before onboarding completes, more than once per session, or anywhere outside the two approved slots.
- The card is visually consistent with the design system except for the `Sponsored` label and the missing toggle.
- AdMob reporting shows impressions with no policy warnings; only test units are used in debug and internal builds.
- Analytics events fire only after opt-in.
- Crashlytics receives a test crash in an internal build.
- No prohibited data appears in any event or crash key.
- Play pre-launch report has no blocking issues.
- The store listing uses only original art and declares ads accurately.
- A `libre` release contains no Firebase, AdMob, or UMP code.

## 12. Testing plan

### 12.1 Unit tests

| Area | Cases |
|---|---|
| Schedule | Same-day window, overnight window, day boundaries, DST-safe `ZonedDateTime`, disabled |
| Evaluator | Always block, downtime block, pause, ignored package, own package |
| Scroll accumulator | Threshold per sensitivity, cooldown, reset on inactivity, horizontal scroll ignored |
| Platform registry | Unique keys, valid packages, valid hosts, widget keys resolve |
| Domain matcher | Exact, subdomain, `www`, case, trailing dot, IP, invalid, custom list merge |
| DNS message | Query parse, NXDOMAIN build, ID preservation, OPT echo, truncated packet, non-DNS packet |
| Deep link | Valid platform, unknown platform, malformed URI |
| Counters | Daily rollover, pruning, aggregation, no double counting |
| Analytics | Event schema, consent gating, no free text, user property mapping |
| Ad frequency | Pre-onboarding block, per-session cap, per-day cap, cooldown, offline, day rollover |
| Ad consent | No request before UMP resolves, non-personalized fallback, `libre` never requests |

### 12.2 Instrumented and manual tests

| Feature | Steps | Expected |
|---|---|---|
| Onboarding | Complete all 12 steps | Settings match summary, Home personalized |
| Onboarding resume | Kill app mid-flow, reopen | Resumes at last incomplete step |
| App blocker | Select app, open it | Overlay within 1.5 s |
| Downtime | Set window starting now, open blocked app | Overlay with downtime reason |
| Pause | Pause 5 min, open blocked app | App opens normally until pause expires |
| IG Reels | Scroll Reels past budget | Overlay, cooldown honored |
| IG DMs | Open DM, scroll | No overlay |
| YT Shorts | Scroll Shorts past budget | Overlay |
| YT normal video | Watch and scroll home | No overlay |
| TikTok FYP | Scroll past budget | Overlay (beta) |
| VPN blocked | Enable social, open instagram.com in browser | Site fails to resolve |
| VPN allowed | Open example.com | Loads normally |
| VPN custom | Add example.org, browse it | Fails; other sites unaffected |
| VPN revoke | Revoke in system settings | Service stops, UI updates |
| Widgets | Add each widget, tap | Correct platform opens |
| Web policy | Tap external link | Opens Custom Tab |
| Login | Sign in to a supported platform | Session persists |
| Reboot | Reboot device | Accessibility services re-bind; VPN state is as expected |
| Analytics off | Fresh install, decline consent, use app | No Firebase events or crashes sent |
| Analytics on | Accept consent, complete onboarding | DebugView shows the expected funnel |
| Visual | Screenshot all key screens | Matches tokens, hierarchy, and shadow rules |
| TalkBack | Navigate Home and overlay | All controls labeled and reachable |
| Battery | Leave services on for 1 hour | No excessive drain or ANR |
| Sponsor card | Scroll Home to the bottom with caps available | One labeled card in the same visual language, no layout shift |
| Ad caps | Reopen Home five times in one session | At most one impression |
| Ad offline | Airplane mode, open Home | No card, no error, no empty box |
| Libre build | Install `libreDebug`, open Home | No ad request, no Firebase init |
| DebugView | Open the first Phase 0 build | Test event and test crash arrive |

### 12.3 Device matrix

- Redmi `garnet`, Android 16 (physical, already connected).
- Emulator API 29 (minimum).
- Emulator API 33.
- Emulator or physical API 36.
- AOSP-like emulator for OEM-killer differences.

### 12.4 Play testing

- Internal testing track with a clean install and an upgrade install.
- Pre-launch report on at least three devices.
- Data Safety questionnaire reviewed against the event taxonomy.
- Accessibility and VPN declaration videos recorded on a real device.
- Staged rollout monitored for 48 hours per stage.

### 12.5 CI gates

- Build, unit tests, lint, ktlint, and Roborazzi screenshot tests on every pull request.
- Android Lint warnings treated as errors for new code.
- No test may depend on the network.
- Release workflow injects Firebase config from secrets and never prints it.
- Release workflow uploads the mapping file to Crashlytics.
- CI builds both flavors and fails if the `libre` runtime classpath contains Firebase, AdMob, or UMP.

## 13. Risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Social app updates change view IDs | Reels detection stops | Per-platform fallback heuristic, debug diagnostics, toggles, versioned detection profiles |
| Accessibility service disabled by OEM | Blocking stops silently | Home status card, periodic health check, battery guidance |
| Background activity launch refused | Overlay does not appear | Test on API 36, fall back to a `SYSTEM_ALERT_WINDOW` overlay with explicit opt-in |
| DNS filtering bypassed by DoH/DoT | Web blocker incomplete | Document clearly, optionally add DoH endpoint blocking later |
| WebView login rejected | User cannot sign in | "Open in browser" fallback, explain limitations |
| Play rejects accessibility or VPN use | Launch blocked | In-app disclosures, core-function justification, video demos, staged declarations |
| Data Safety mismatch | Policy violation or removal | Single analytics event registry, QA audit, no hidden SDKs |
| Analytics consent not honored | GDPR and trust damage | Consent gate at the SDK boundary, tests, no events before consent |
| Firebase config leaks or is abused | Cost or quota risk | Do not commit production config, use CI secrets, restrict API key by package and SHA |
| Ads damage the calm feel | Trust loss, uninstalls | Native cards only, two slots, hard caps, nothing in blocking moments, watch complaints and retention |
| Ad revenue is negligible at first | No funding | Keep infrastructure near zero cost on the Firebase free tier; treat revenue as upside, never as survival |
| AdMob policy violation or account ban | Lost revenue, release blocked | Test units in dev, labeled card, AdChoices intact, no accidental-click placement, never tap live ads yourself |
| UMP consent missed for EEA or UK | GDPR exposure | Consent resolved before the first ad request, non-personalized fallback, Data Safety matches the implementation |
| Ads perceived as a bait and switch | Review complaints | Disclose ads in the onboarding summary and the store listing before install |
| Object illustrations feel inconsistent | Premium feel breaks | One art direction doc, one generation pipeline, review every asset before check-in |
| Visual scope creep | Slow build, inconsistent UI | Design tokens and shared components only, no one-off styles |
| Light-only theme complaints | Lower satisfaction | Fast-follow warm dark theme in v1.1 |
| Trademark or copyright complaint | Takedown | Own brand, own icons, clean-room code, no copied assets or strings |
| Battery drain | User uninstalls | Event-driven services, no polling in content blocker, 15 s tick only in app blocker |
| Data loss | Settings lost | DataStore plus optional JSON export/import |
| Accidental blocking of launcher/IME | Unusable device | Strict ignored-package list and tests |

## 14. Definition of done for v1.0.0

- Features 1, 2, 3, 5, and 7 work on the Redmi and at least two emulator API levels.
- Onboarding is conversational, personalized, and completes in under two minutes.
- The UI matches the visual system: off-white canvas, floating cards, pastel accents, object illustrations, and no harsh borders or default Material styling.
- Analytics is opt-in, event taxonomy is documented, and no prohibited data is sent.
- Crashlytics reports crashes from internal builds and respects consent.
- No billing, accounts, kid mode, backend, or unused SDKs exist in the repository.
- Ads exist only as the two capped sponsor slots, labeled, and never in onboarding, the app picker, the web view, or the block overlay.
- A `libre` build runs the full app with no proprietary SDKs.
- Every permission is explained and optional unless required for the feature the user enabled.
- Unit tests, screenshot tests, lint, and ktlint pass in CI.
- The manual test checklist is completed.
- README, LICENSE, privacy statement, analytics doc, and known limitations are published.
- Play Data Safety and policy declarations are completed and match the implementation.
- A tagged release AAB installs from Play internal testing and a GitHub APK installs on a clean device.

## 15. Post-v1 backlog

- Warm charcoal dark theme.
- IPv6 DNS interception if device testing shows bypass.
- Per-app VPN exclusions for banking and work apps.
- Additional native detectors for Reddit, LinkedIn, and X.
- Import/export of settings.
- Optional local usage insights without `PACKAGE_USAGE_STATS`.
- Localization.
- Remote config for onboarding copy, only if it can be done without user data.
- F-Droid reproducible build verification.
- Optional one-tap donation card beside the sponsor slot, for people who would rather pay than look.
- Remove-ads purchase, only if billing ever comes back into scope.

## 16. Approval checklist

- [ ] D1 name: Reclaim
- [ ] D2 application ID: `dev.reclaim.app`
- [ ] D3 license: GPL-3.0 or Apache-2.0
- [ ] D4 minimum SDK: 29
- [ ] D5 v1 platform set
- [ ] D6 primary vs beta native detectors
- [ ] D7 notification suppression included
- [ ] D8 emergency pause included
- [ ] D9 analytics stack: Firebase Analytics plus Crashlytics
- [ ] D10 analytics consent: opt-in, default off
- [ ] D11 crash reporting follows analytics consent
- [ ] D12 light-only v1
- [ ] D13 typography: PT Serif
- [ ] D14 icon system: Font Awesome Free solid
- [ ] D15 no mascot
- [ ] D16 Google Play first
- [ ] D17 target audience 13+
- [ ] D18 own platform glyphs
- [ ] D19 ad format: AdMob native sponsor cards
- [ ] D20 ad surfaces: Home bottom and About only
- [ ] D21 ad labeling: `Sponsored` plus AdChoices
- [ ] D22 ad consent: UMP with non-personalized fallback
- [ ] D23 build flavors: `play` and `libre`
- [ ] D24 no paid ad removal in v1
- [ ] D25 test ad units until Phase 9
- [ ] D26 design review uses the adapted unslop checklist
- [ ] Plan approved to start Phase 0

Reply with approvals or changes, and implementation begins with Phase 0.
