package org.freedomtech.accountability

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MonitorAccessibilityService : AccessibilityService() {
    private val tracker = VisitCountTracker()

    private val browserPackages = setOf(
        "app.vanadium.browser",
        "com.android.chrome",
        "com.brave.browser",
        "org.mozilla.firefox",
        "org.mozilla.focus",
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return
        val isBrowser = packageName in browserPackages

        val visibleText = collectVisibleText(rootInActiveWindow, limit = 80)
        val urlIdentity = visibleText.asSequence()
            .mapNotNull { DomainExtractor.normalizeUrlIdentity(it) }
            .firstOrNull { identity ->
                val domain = DomainExtractor.normalizeDomain(identity)
                domain != null && DomainExtractor.isTracked(
                    domain,
                    setOf("x.com", "twitter.com", "youtube.com", "m.youtube.com", "youtu.be")
                )
            }

        val increment = tracker.observe(
            VisitObservation(
                timestampMs = System.currentTimeMillis(),
                urlIdentity = urlIdentity,
                foreground = isBrowser,
            )
        )

        if (increment != null) {
            persistIncrement(increment)
            android.util.Log.i("DAMonitor", "visit=${increment.domain} day=${increment.day} totals=${readDayTotals(increment.day)}")
        }
    }

    override fun onInterrupt() = Unit

    private fun persistIncrement(increment: VisitIncrement) {
        val prefs = getSharedPreferences("visit-counts", Context.MODE_PRIVATE)
        val key = "${increment.day}:${increment.domain}"
        prefs.edit().putInt(key, prefs.getInt(key, 0) + increment.count).apply()
    }

    private fun readDayTotals(day: String): Map<String, Int> {
        val prefix = "$day:"
        return getSharedPreferences("visit-counts", Context.MODE_PRIVATE)
            .all
            .filterKeys { it.startsWith(prefix) }
            .mapKeys { it.key.removePrefix(prefix) }
            .mapValues { (_, value) -> value as? Int ?: 0 }
    }

    private fun collectVisibleText(node: AccessibilityNodeInfo?, limit: Int): List<String> {
        if (node == null || limit <= 0) return emptyList()
        val out = mutableListOf<String>()
        fun visit(n: AccessibilityNodeInfo?) {
            if (n == null || out.size >= limit) return
            n.text?.toString()?.takeIf { it.isNotBlank() }?.let(out::add)
            n.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let(out::add)
            for (i in 0 until n.childCount) visit(n.getChild(i))
        }
        visit(node)
        return out
    }
}
