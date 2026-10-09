package id.rammonitor.simple

import android.app.Activity
import android.app.ActivityManager
import android.content.pm.ApplicationInfo
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import java.util.Locale
import kotlin.math.max

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var ramSummary: TextView
    private lateinit var ramChart: RamChartView
    private lateinit var appList: LinearLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var sortSpinner: Spinner
    private var allApps: List<AppRow> = emptyList()
    private var totalMb = 0L
    private var usedMb = 0L
    private var availableMb = 0L

    data class AppRow(val label: String, val packageName: String, val isSystem: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        refreshData()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun text(value: String, size: Float, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(0xFF172033.toInt())
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

    private fun buildUi() {
        val scroll = ScrollView(this)
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(24))
            setBackgroundColor(0xFFF5F7FB.toInt())
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(text("RAM Monitor", 27f, true))
        root.addView(text("Pantau memori ponsel secara lokal", 14f).apply {
            setTextColor(0xFF5C667A.toInt())
            setPadding(0, dp(3), 0, dp(16))
        })

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        root.addView(card, LinearLayout.LayoutParams(-1, -2))
        card.addView(text("Penggunaan RAM perangkat", 17f, true))
        ramSummary = text("Membaca status memori…", 15f)
        ramSummary.setPadding(0, dp(8), 0, dp(8))
        card.addView(ramSummary)
        ramChart = RamChartView(this)
        card.addView(ramChart, LinearLayout.LayoutParams(-1, dp(28)))
        card.addView(text("Grafik menampilkan snapshot saat ini, bukan riwayat.", 12f).apply {
            setTextColor(0xFF687386.toInt())
            setPadding(0, dp(8), 0, 0)
        })

        root.addView(text("Daftar aplikasi", 20f, true).apply {
            setPadding(0, dp(22), 0, dp(10))
        })

        categorySpinner = Spinner(this)
        categorySpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Semua aplikasi", "Aplikasi pengguna", "Aplikasi sistem")
        )
        root.addView(categorySpinner, LinearLayout.LayoutParams(-1, dp(48)))

        sortSpinner = Spinner(this)
        sortSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Urutkan: nama A–Z", "Urutkan: sistem dahulu", "Urutkan: RAM (data tersedia)")
        )
        root.addView(sortSpinner, LinearLayout.LayoutParams(-1, dp(48)))

        appList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(appList, LinearLayout.LayoutParams(-1, -2))

        categorySpinner.onItemSelectedListener = SimpleItemSelected { renderApps() }
        sortSpinner.onItemSelectedListener = SimpleItemSelected { renderApps() }
    }

    private fun refreshData() {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(info)
        totalMb = info.totalMem / (1024 * 1024)
        availableMb = info.availMem / (1024 * 1024)
        usedMb = max(0L, totalMb - availableMb)
        val percent = if (totalMb > 0) (usedMb * 100 / totalMb) else 0
        ramSummary.text = "Terpakai: ${usedMb} MB ($percent%)\\nTersedia: ${availableMb} MB\\nTotal: ${totalMb} MB" +
            if (info.lowMemory) "\\nPeringatan: Android menandai kondisi memori rendah." else ""
        ramChart.setValues(usedMb.toFloat(), availableMb.toFloat())

        allApps = try {
            packageManager.getInstalledApplications(0).map { app ->
                val label = try { packageManager.getApplicationLabel(app).toString() }
                    catch (_: Exception) { app.packageName }
                AppRow(
                    label = label.ifBlank { app.packageName },
                    packageName = app.packageName,
                    isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                        (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                )
            }.sortedBy { it.label.lowercase(Locale.ROOT) }
        } catch (_: Exception) {
            emptyList()
        }
        renderApps()
    }

    private fun renderApps() {
        if (!::appList.isInitialized) return
        appList.removeAllViews()
        val category = if (::categorySpinner.isInitialized) categorySpinner.selectedItemPosition else 0
        val sort = if (::sortSpinner.isInitialized) sortSpinner.selectedItemPosition else 0
        var filtered = allApps.filter {
            when (category) {
                1 -> !it.isSystem
                2 -> it.isSystem
                else -> true
            }
        }
        filtered = when (sort) {
            1 -> filtered.sortedWith(compareByDescending<AppRow> { it.isSystem }.thenBy { it.label.lowercase(Locale.ROOT) })
            // Android restricts ordinary apps from reading other apps' process RAM. Keep unavailable
            // values clearly labeled rather than inventing or estimating per-app numbers.
            2 -> filtered.sortedBy { it.label.lowercase(Locale.ROOT) }
            else -> filtered.sortedBy { it.label.lowercase(Locale.ROOT) }
        }

        appList.addView(text("${filtered.size} aplikasi ditampilkan", 13f).apply {
            setTextColor(0xFF687386.toInt())
            setPadding(0, dp(8), 0, dp(8))
        })
        filtered.forEach { item ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14), dp(12), dp(14), dp(12))
                setBackgroundColor(0xFFFFFFFF.toInt())
            }
            val lp = LinearLayout.LayoutParams(-1, -2)
            lp.bottomMargin = dp(8)
            appList.addView(card, lp)

            val title = text(item.label, 15f, true)
            title.maxLines = 1
            title.ellipsize = TextUtils.TruncateAt.END
            card.addView(title)
            card.addView(text(if (item.isSystem) "Aplikasi sistem/bawaan" else "Aplikasi pengguna", 12f).apply {
                setTextColor(0xFF4E647F.toInt())
                setPadding(0, dp(3), 0, dp(2))
            })
            val pkg = text(item.packageName, 11f)
            pkg.setTextColor(0xFF7A8495.toInt())
            pkg.maxLines = 1
            pkg.ellipsize = TextUtils.TruncateAt.END
            card.addView(pkg)
            card.addView(text("RAM per aplikasi: tidak tersedia untuk aplikasi biasa di Android modern", 12f).apply {
                setTextColor(0xFF9A5A16.toInt())
                setPadding(0, dp(5), 0, 0)
            })
        }
    }

    private class RamChartView(context: android.content.Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var used = 1f
        private var available = 1f

        fun setValues(usedValue: Float, availableValue: Float) {
            used = max(0f, usedValue)
            available = max(0f, availableValue)
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val total = max(1f, used + available)
            val usedWidth = width * (used / total)
            paint.color = 0xFFE2E8F0.toInt()
            canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), height / 2f, height / 2f, paint)
            paint.color = if (used / total > 0.85f) 0xFFD94C4C.toInt() else 0xFF3978E5.toInt()
            canvas.drawRoundRect(RectF(0f, 0f, usedWidth, height.toFloat()), height / 2f, height / 2f, paint)
        }
    }

    private class SimpleItemSelected(private val action: () -> Unit) :
        android.widget.AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) = action()
        override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
    }
}
