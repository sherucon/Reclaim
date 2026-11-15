package dev.reclaim.app.domain.model

/**
 * Single source of truth for platform lookups. Keys, package names, and host
 * roots must stay unique, or one entry silently shadows another.
 */
object PlatformRegistry {
    val all: List<Platform> = Platform.entries

    private val byKey: Map<String, Platform> = all.associateBy { it.key }

    private val byPackage: Map<String, Platform> =
        all.flatMap { platform -> platform.packages.map { it to platform } }.toMap()

    init {
        check(byKey.size == all.size) { "Duplicate platform key" }
        val packages = all.flatMap { it.packages }
        check(packages.size == packages.toSet().size) { "Duplicate platform package" }
        val hosts = all.flatMap { it.webHosts }
        check(hosts.size == hosts.toSet().size) { "Duplicate platform host" }
    }

    fun byKey(key: String?): Platform? = key?.trim()?.lowercase()?.let(byKey::get)

    fun byPackage(packageName: String?): Platform? = packageName?.let(byPackage::get)

    fun byHost(host: String): Platform? {
        val normalized = normalizeHost(host) ?: return null
        return all.firstOrNull { platform ->
            platform.webHosts.any { root -> normalized == root || normalized.endsWith(".$root") }
        }
    }

    fun requireKey(key: String?): Platform? = byKey(key)

    private fun normalizeHost(raw: String): String? {
        var host = raw.trim().lowercase()
        if (host.isEmpty()) return null
        if (host.endsWith(".")) host = host.dropLast(1)
        if (host.startsWith("www.")) host = host.removePrefix("www.")
        return host.ifEmpty { null }
    }
}
