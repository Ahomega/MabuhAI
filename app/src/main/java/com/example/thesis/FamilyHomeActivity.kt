package com.example.thesis

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class FamilyHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_home)

        val ecgChart = findViewById<LineChart>(R.id.ecgChart)
        val ppgChart = findViewById<LineChart>(R.id.ppgChart)

        setupChart(ecgChart, "ECG Signal", Color.RED)
        setupChart(ppgChart, "PPG Signal", Color.GREEN)
    }

    private fun setupChart(chart: LineChart, label: String, color: Int) {
        val entries = ArrayList<Entry>()
        for (i in 0..150) {
            val y = if (label == "ECG Signal")
                (Math.sin(i * 0.2) * 100).toFloat()
            else
                (Math.cos(i * 0.08) * 50 + 100).toFloat()
            entries.add(Entry(i.toFloat(), y))
        }

        val set = LineDataSet(entries, label)
        set.color = color
        set.setDrawCircles(false)
        set.lineWidth = 2f

        chart.data = LineData(set)
        chart.description.isEnabled = false
        chart.invalidate()
    }
}
