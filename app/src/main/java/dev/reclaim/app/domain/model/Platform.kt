package dev.reclaim.app.domain.model

/**
 * How far Reclaim can go inside a platform's native app.
 */
enum class DetectionStrength {
    /** Stable view IDs or a package-gated surface. */
    Strong,

    /** Scroll heuristic only, may misfire. */
    Heuristic,

    /** Web and widgets only, no native detection. */
    None,
}

/**
 * The platforms Reclaim knows about. One entry feeds the blocker, the widget,
 * the web layer, and onboarding.
 */
enum class Platform(
    val key: String,
    val displayName: String,
    val packages: List<String>,
    val webHosts: List<String>,
    val homeUrl: String,
    val detection: DetectionStrength,
) {
    INSTAGRAM(
        key = "instagram",
        displayName = "Instagram",
        packages = listOf("com.instagram.android"),
        webHosts = listOf("instagram.com"),
        homeUrl = "https://www.instagram.com/",
        detection = DetectionStrength.Strong,
    ),
    YOUTUBE(
        key = "youtube",
        displayName = "YouTube",
        packages = listOf("com.google.android.youtube"),
        webHosts = listOf("youtube.com", "youtu.be", "youtube-nocookie.com"),
        homeUrl = "https://m.youtube.com/",
        detection = DetectionStrength.Strong,
    ),
    TIKTOK(
        key = "tiktok",
        displayName = "TikTok",
        packages = listOf("com.zhiliaoapp.musically", "com.ss.android.ugc.trill"),
        webHosts = listOf("tiktok.com"),
        homeUrl = "https://www.tiktok.com/",
        detection = DetectionStrength.Heuristic,
    ),
    FACEBOOK(
        key = "facebook",
        displayName = "Facebook",
        packages = listOf("com.facebook.katana"),
        webHosts = listOf("facebook.com", "fb.com"),
        homeUrl = "https://m.facebook.com/",
        detection = DetectionStrength.Heuristic,
    ),
    SNAPCHAT(
        key = "snapchat",
        displayName = "Snapchat",
        packages = listOf("com.snapchat.android"),
        webHosts = listOf("snapchat.com"),
        homeUrl = "https://web.snapchat.com/",
        detection = DetectionStrength.Heuristic,
    ),
    X(
        key = "x",
        displayName = "X",
        packages = listOf("com.twitter.android"),
        webHosts = listOf("x.com", "twitter.com"),
        homeUrl = "https://x.com/",
        detection = DetectionStrength.None,
    ),
    LINKEDIN(
        key = "linkedin",
        displayName = "LinkedIn",
        packages = listOf("com.linkedin.android"),
        webHosts = listOf("linkedin.com"),
        homeUrl = "https://www.linkedin.com/",
        detection = DetectionStrength.None,
    ),
    REDDIT(
        key = "reddit",
        displayName = "Reddit",
        packages = listOf("com.reddit.frontpage"),
        webHosts = listOf("reddit.com"),
        homeUrl = "https://www.reddit.com/",
        detection = DetectionStrength.None,
    ),
}
