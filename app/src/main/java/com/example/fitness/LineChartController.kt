package com.example.fitness

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class LineChartController(val lineChart: LineChart) {
    fun drawLineChart(data: List<Int>) {
        val lineEntries = toDataSet(data)
        val lineDataSet = LineDataSet(lineEntries, "Test")
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        val lineData = LineData(lineDataSet)
        lineChart.apply {

            this.data = lineData
            this.invalidate()

        }
    }

    private fun toDataSet(data: List<Int>): List<Entry> {
        val lineEntries = ArrayList<Entry>()
        data.forEachIndexed { index, i ->
            lineEntries.add(Entry(index.toFloat(), i.toFloat()))
        }
        return lineEntries
    }
}