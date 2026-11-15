package dev.reclaim.app.domain.model

/**
 * The web filter categories a user can turn on.
 */
enum class DomainCategory(
    val key: String,
    val displayName: String,
) {
    SOCIAL(key = "social", displayName = "Social"),
    ADULT(key = "adult", displayName = "Adult"),
    GAMBLING(key = "gambling", displayName = "Gambling"),
    ;

    companion object {
        fun fromKey(key: String?): DomainCategory? =
            entries.firstOrNull { it.key.equals(key?.trim(), ignoreCase = true) }
    }
}
