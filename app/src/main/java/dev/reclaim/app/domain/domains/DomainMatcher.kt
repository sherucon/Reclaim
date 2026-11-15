package dev.reclaim.app.domain.domains

import dev.reclaim.app.domain.model.DomainCategory

/**
 * A blocked host and the list it came from. A null category means the user's
 * own list, which is not tied to a category switch.
 */
data class DomainMatch(
    val domain: String,
    val category: DomainCategory?,
)

/**
 * Matches a DNS question name against the bundled category lists and the user's
 * custom list. Exact hosts and all of their subdomains match; IP literals and
 * malformed names never do.
 */
class DomainMatcher(
    categories: Map<DomainCategory, Set<String>> = emptyMap(),
    customDomains: Set<String> = emptySet(),
) {
    private val byCategory: Map<DomainCategory, Set<String>> = categories
        .mapValues { (_, domains) -> domains.mapNotNull(::normalize).toSet() }
        .filterValues { it.isNotEmpty() }

    private val custom: Set<String> = customDomains.mapNotNull(::normalize).toSet()

    fun match(host: String): DomainMatch? {
        val normalized = normalize(host) ?: return null

        custom.firstOrNull { normalized.matchesDomain(it) }?.let { domain ->
            return DomainMatch(domain = domain, category = null)
        }

        for ((category, domains) in byCategory) {
            domains.firstOrNull { normalized.matchesDomain(it) }?.let { domain ->
                return DomainMatch(domain = domain, category = category)
            }
        }
        return null
    }

    fun match(
        host: String,
        enabledCategories: Set<DomainCategory>,
    ): DomainMatch? {
        val match = match(host) ?: return null
        val category = match.category ?: return match
        return if (category in enabledCategories) match else null
    }

    val domainCount: Int
        get() = byCategory.values.sumOf { it.size } + custom.size

    private fun String.matchesDomain(domain: String): Boolean = this == domain || endsWith(".$domain")

    companion object {
        private const val MAX_HOST_LENGTH = 253

        private val HostPattern =
            Regex("^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)+$")

        /**
         * Lowercase, strip one trailing dot and a leading `www.`, reject
         * anything that is not a plain hostname. Anything with a scheme, port,
         * path, or that is an IP literal, is not a domain and never matches.
         */
        fun normalize(raw: String): String? {
            var host = raw.trim().lowercase()
            if (host.isEmpty() || host.length > MAX_HOST_LENGTH) return null
            if (host.endsWith(".")) host = host.dropLast(1)
            if (host.startsWith("www.")) host = host.removePrefix("www.")
            if (host.isEmpty() || isIpLiteral(host) || !HostPattern.matches(host)) return null
            return host
        }

        private fun isIpLiteral(host: String): Boolean =
            host.all { it.isDigit() || it == '.' } && host.count { it == '.' } == 3
    }
}
