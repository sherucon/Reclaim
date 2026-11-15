package dev.reclaim.app.domain.model

/**
 * Scroll budgets from the Reels screen. Budgets are in screen heights.
 */
enum class Sensitivity(
    val budgetScreens: Float,
    val cooldownSeconds: Int,
) {
    RELAXED(budgetScreens = 3.0f, cooldownSeconds = 60),
    BALANCED(budgetScreens = 1.5f, cooldownSeconds = 30),
    STRICT(budgetScreens = 0.8f, cooldownSeconds = 15),
    ;

    companion object {
        fun fromKey(key: String?): Sensitivity =
            entries.firstOrNull { it.name.equals(key, ignoreCase = true) } ?: BALANCED
    }
}
