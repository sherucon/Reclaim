package dev.reclaim.app.analytics

/**
 * The complete event taxonomy. Names are snake_case and short on purpose: they
 * are the contract with the Data Safety form and the dashboards.
 *
 * Never add free text, names, package names, domains, URLs, page titles, or
 * notification content. Parameters are enums, booleans, or bucketed numbers.
 */
object AnalyticsEvents {
    const val APP_OPEN = "app_open"
    const val SCREEN_VIEW = "screen_view"

    const val ONBOARDING_STARTED = "onboarding_started"
    const val ONBOARDING_STEP_VIEWED = "onboarding_step_viewed"
    const val ONBOARDING_STEP_COMPLETED = "onboarding_step_completed"
    const val ONBOARDING_SKIPPED = "onboarding_skipped"
    const val ONBOARDING_COMPLETED = "onboarding_completed"

    const val PERMISSION_PROMPTED = "permission_prompted"
    const val PERMISSION_RESULT = "permission_result"

    const val FEATURE_TOGGLED = "feature_toggled"
    const val APPS_SELECTED_BUCKET = "apps_selected_bucket"
    const val DOWNTIME_SET = "downtime_set"
    const val BLOCK_OVERLAY_SHOWN = "block_overlay_shown"

    const val REELS_PLATFORM_TOGGLED = "reels_platform_toggled"
    const val REELS_SENSITIVITY_SET = "reels_sensitivity_set"
    const val REELS_BREAK_SHOWN = "reels_break_shown"

    const val WEB_CATEGORY_TOGGLED = "web_category_toggled"
    const val WEB_CUSTOM_DOMAINS_BUCKET = "web_custom_domains_bucket"
    const val VPN_STARTED = "vpn_started"
    const val VPN_REVOKED = "vpn_revoked"

    const val WIDGET_ADDED = "widget_added"
    const val WIDGET_TAPPED = "widget_tapped"

    const val WEB_PLATFORM_OPENED = "web_platform_opened"
    const val WEB_SCRIPT_FAILED = "web_script_failed"

    const val DAILY_STATS = "daily_stats"

    val allNames: Set<String> = setOf(
        APP_OPEN,
        SCREEN_VIEW,
        ONBOARDING_STARTED,
        ONBOARDING_STEP_VIEWED,
        ONBOARDING_STEP_COMPLETED,
        ONBOARDING_SKIPPED,
        ONBOARDING_COMPLETED,
        PERMISSION_PROMPTED,
        PERMISSION_RESULT,
        FEATURE_TOGGLED,
        APPS_SELECTED_BUCKET,
        DOWNTIME_SET,
        BLOCK_OVERLAY_SHOWN,
        REELS_PLATFORM_TOGGLED,
        REELS_SENSITIVITY_SET,
        REELS_BREAK_SHOWN,
        WEB_CATEGORY_TOGGLED,
        WEB_CUSTOM_DOMAINS_BUCKET,
        VPN_STARTED,
        VPN_REVOKED,
        WIDGET_ADDED,
        WIDGET_TAPPED,
        WEB_PLATFORM_OPENED,
        WEB_SCRIPT_FAILED,
        DAILY_STATS,
    )

    object Params {
        const val SCREEN_NAME = "screen_name"
        const val STEP_ID = "step_id"
        const val STEP_INDEX = "step_index"
        const val OPTION_CODE = "option_code"
        const val DURATION_MS = "duration_ms"
        const val DURATION_SEC = "duration_sec"
        const val PERMISSION = "permission"
        const val GRANTED = "granted"
        const val FEATURE = "feature"
        const val ENABLED = "enabled"
        const val BUCKET = "bucket"
        const val DAYS_COUNT = "days_count"
        const val HAS_OVERNIGHT = "has_overnight"
        const val PLATFORM = "platform"
        const val LEVEL = "level"
        const val KIND = "kind"
        const val REASON = "reason"
        const val SCROLL_BUCKET = "scroll_bucket"
        const val CATEGORY = "category"
        const val CATEGORY_COUNT = "category_count"
        const val TYPE = "type"
        const val REELS_BREAKS = "reels_breaks"
        const val APP_BLOCKS = "app_blocks"
        const val DNS_BLOCKS = "dns_blocks"
        const val ACTIVE_SERVICES = "active_services"

        val allKeys: Set<String> = setOf(
            SCREEN_NAME,
            STEP_ID,
            STEP_INDEX,
            OPTION_CODE,
            DURATION_MS,
            DURATION_SEC,
            PERMISSION,
            GRANTED,
            FEATURE,
            ENABLED,
            BUCKET,
            DAYS_COUNT,
            HAS_OVERNIGHT,
            PLATFORM,
            LEVEL,
            KIND,
            REASON,
            SCROLL_BUCKET,
            CATEGORY,
            CATEGORY_COUNT,
            TYPE,
            REELS_BREAKS,
            APP_BLOCKS,
            DNS_BLOCKS,
            ACTIVE_SERVICES,
        )
    }
}
