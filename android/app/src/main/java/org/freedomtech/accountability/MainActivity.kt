package org.freedomtech.accountability

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val today = LocalDate.now().toString()
        val totals = readDayTotals(today)
        val serviceOn = isAccessibilityServiceEnabled()
        val lastVisit = readLastVisitText()
        val trackedSites = listOf("x.com", "youtube.com").joinToString(", ")
        val totalsText = if (totals.isEmpty()) {
            "No visits counted today. Open a tracked site in Vanadium, Brave, Chrome, or Firefox."
        } else {
            totals.entries
                .sortedByDescending { it.value }
                .joinToString("\n") { "${it.key}: ${it.value} visits" }
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 72, 40, 48)
        }

        content.addView(TextView(this).apply {
            text = "Digital Mindfulness"
            textSize = 26f
        })
        content.addView(TextView(this).apply {
            text = "\nCount how often you visit distracting websites. The app counts visits by calendar day, not time spent."
            textSize = 18f
        })
        content.addView(TextView(this).apply {
            text = "\nStatus\nAccessibility service: ${if (serviceOn) "On" else "Off"}\nSync: Off, data stays on this device\nTracked sites: $trackedSites\nLast counted visit: $lastVisit"
            textSize = 16f
        })
        content.addView(TextView(this).apply {
            text = "\nToday, $today\n$totalsText"
            textSize = 18f
        })
        content.addView(TextView(this).apply {
            text = "\nBefore enabling Accessibility\nAndroid will warn that this app can see screen content. That is true. Digital Mindfulness uses that access only to look for supported browser pages and count visits to tracked sites. It does not send data anywhere in this version."
            textSize = 16f
        })
        content.addView(TextView(this).apply {
            text = "\nWhat counts as a visit\nThe same open tab does not count again. Changing page, leaving and coming back, or starting a new local day counts again."
            textSize = 16f
        })
        content.addView(Button(this).apply {
            text = if (serviceOn) "Review Accessibility Settings" else "Enable Accessibility Service"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })
        content.addView(Button(this).apply {
            text = "Refresh totals"
            setOnClickListener { render() }
        })
        content.addView(Button(this).apply {
            text = "Reset today's counts"
            setOnClickListener {
                resetDay(today)
                render()
            }
        })

        setContentView(ScrollView(this).apply { addView(content) })
    }

    private fun readDayTotals(day: String): Map<String, Int> {
        val prefix = "$day:"
        return getSharedPreferences("visit-counts", Context.MODE_PRIVATE)
            .all
            .filterKeys { it.startsWith(prefix) }
            .mapKeys { it.key.removePrefix(prefix) }
            .mapValues { (_, value) -> value as? Int ?: 0 }
    }

    private fun resetDay(day: String) {
        val prefix = "$day:"
        val prefs = getSharedPreferences("visit-counts", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith(prefix) }.forEach(editor::remove)
        editor.apply()
    }

    private fun readLastVisitText(): String {
        val timestamp = getSharedPreferences("visit-counts", Context.MODE_PRIVATE)
            .getLong("last_visit_at", 0L)
        if (timestamp <= 0L) return "None yet"
        return DateTimeFormatter.ofPattern("MMM d, h:mm a")
            .format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expected = ComponentName(this, MonitorAccessibilityService::class.java).flattenToString()
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return TextUtils.SimpleStringSplitter(':').run {
            setString(enabled)
            any { it.equals(expected, ignoreCase = true) }
        }
    }
}
