package org.freedomtech.accountability

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.time.LocalDate

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
        val totalsText = if (totals.isEmpty()) {
            "No visits counted yet today."
        } else {
            totals.entries
                .sortedByDescending { it.value }
                .joinToString("\n") { "${it.key}: ${it.value}" }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
        }
        layout.addView(TextView(this).apply {
            text = "Digital Mindfulness\n\nCounts visits to selected websites per local calendar day. Same open tab + same URL does not recount; URL changes, reopening, or the next local day counts again.\n\nToday ($today):\n$totalsText"
            textSize = 18f
        })
        layout.addView(Button(this).apply {
            text = "Open Accessibility Settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })
        layout.addView(Button(this).apply {
            text = "Refresh totals"
            setOnClickListener { render() }
        })
        setContentView(layout)
    }

    private fun readDayTotals(day: String): Map<String, Int> {
        val prefix = "$day:"
        return getSharedPreferences("visit-counts", Context.MODE_PRIVATE)
            .all
            .filterKeys { it.startsWith(prefix) }
            .mapKeys { it.key.removePrefix(prefix) }
            .mapValues { (_, value) -> value as? Int ?: 0 }
    }
}
