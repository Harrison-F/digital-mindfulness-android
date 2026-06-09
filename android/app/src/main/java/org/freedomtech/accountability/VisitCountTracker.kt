package org.freedomtech.accountability

import java.time.Instant
import java.time.ZoneId

/**
 * Counts website visits by local calendar day.
 *
 * Visit semantics for the mobile MVP:
 * - same foreground browser + same visible URL/domain does not recount on repeated accessibility events
 * - changing URL/domain counts a new visit
 * - leaving the browser and later returning to the same URL/domain counts a new visit
 * - crossing into a new local calendar day counts a new visit
 */
data class VisitObservation(
    val timestampMs: Long,
    val urlIdentity: String?,
    val foreground: Boolean,
)

data class VisitIncrement(
    val day: String,
    val urlIdentity: String,
    val domain: String,
    val count: Int,
)

class VisitCountTracker(
    private val trackedDomains: Set<String> = setOf("x.com", "twitter.com", "youtube.com", "m.youtube.com", "youtu.be"),
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) {
    private var activeIdentity: String? = null
    private var activeDay: String? = null

    fun observe(obs: VisitObservation): VisitIncrement? {
        val identity = DomainExtractor.normalizeUrlIdentity(obs.urlIdentity)
        val domain = DomainExtractor.normalizeDomain(identity)
        val day = localDay(obs.timestampMs)

        if (!obs.foreground || identity == null || domain == null || !DomainExtractor.isTracked(domain, trackedDomains)) {
            activeIdentity = null
            activeDay = null
            return null
        }

        if (identity == activeIdentity && day == activeDay) return null

        activeIdentity = identity
        activeDay = day
        return VisitIncrement(day = day, urlIdentity = identity, domain = domain, count = 1)
    }

    private fun localDay(timestampMs: Long): String = Instant
        .ofEpochMilli(timestampMs)
        .atZone(zoneId)
        .toLocalDate()
        .toString()
}
