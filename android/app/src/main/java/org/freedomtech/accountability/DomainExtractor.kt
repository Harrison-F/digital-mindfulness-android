package org.freedomtech.accountability

object DomainExtractor {
    private val urlRegex = Regex("""(?i)(https?://)?(www\.)?([a-z0-9-]+\.)+[a-z]{2,}[^\s]*""")

    fun normalizeUrlIdentity(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val match = urlRegex.find(raw.trim().lowercase())?.value ?: return null
        var value = match
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore('#')
            .trimEnd('/')
        if (value.startsWith("www.")) value = value.removePrefix("www.")
        return value.takeIf { domainFromIdentity(it) != null }
    }

    fun normalizeDomain(raw: String?): String? {
        val value = normalizeUrlIdentity(raw) ?: raw?.trim()?.lowercase() ?: return null
        return domainFromIdentity(value)
    }

    private fun domainFromIdentity(identity: String): String? {
        var value = identity
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore('/')
            .substringBefore(':')
        if (value.startsWith("www.")) value = value.removePrefix("www.")
        return value.takeIf { it.contains('.') }
    }

    fun isTracked(domain: String, trackedDomains: Set<String>): Boolean {
        return domain in trackedDomains
    }
}
