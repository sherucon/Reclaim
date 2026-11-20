package dev.reclaim.app.domain.model

/**
 * Why the overlay appeared. The overlay picks its copy and its gradient from it.
 */
enum class BlockReason(
    val code: String,
) {
    ALWAYS("always"),
    DOWNTIME("downtime"),
}
