package com.example.fitness

import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*

class LineChartController(val lineChart: LineChart) {
    fun drawLineChart(data: List<Int>) {
        val lineEntries = toDataSet(data)
        val lineDataSet = LineDataSet(lineEntries, "Test")
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor =
            ResourcesCompat.getColor(lineChart.resources, R.color.graph_line_color, null)
        lineDataSet.color =
            ResourcesCompat.getColor(lineChart.resources, R.color.graph_line_color, null)
        lineDataSet.setCircleColor(
            ResourcesCompat.getColor(
                lineChart.resources,
                R.color.graph_line_color,
                null
            )
        )
        lineDataSet.setFillAlpha(80);
        val lineData = LineData(lineDataSet)
        lineChart.apply {

            this.data = lineData
            this.invalidate()

        }
        lineChart.getAxisLeft().setDrawGridLines(false)
        lineChart.getXAxis().setDrawGridLines(false)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val lable_list = mutableListOf<String>()
        for (i in 8..15) {
            lable_list.add(days.get((i - today) % 6))
        }
        lineChart.xAxis.setValueFormatter(IndexAxisValueFormatter(lable_list))
    }

    private fun toDataSet(data: List<Int>): List<Entry> {
        val lineEntries = ArrayList<Entry>()
        data.forEachIndexed { index, i ->
            lineEntries.add(Entry(index.toFloat(), i.toFloat()))
        }
        return lineEntries
    }
}