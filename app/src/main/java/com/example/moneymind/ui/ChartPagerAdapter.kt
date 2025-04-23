package com.example.moneymind.ui.charts

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate

class ChartPagerAdapter(
    private val context: Context,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<ChartPagerAdapter.ChartViewHolder>() {

    private var data: List<PieEntry> = emptyList()
    private var totalAmount: Float = 0f

    private var pieChartRef: PieChart? = null // 💡 Сохраняем ссылку на круговую диаграмму

    fun setChartData(entries: List<PieEntry>, total: Float) {
        data = entries
        totalAmount = total
        notifyDataSetChanged()
    }

    fun getPieChart(): PieChart? = pieChartRef // 🔹 Получить текущую круговую диаграмму

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val chart: View = when (viewType) {
            0 -> PieChart(context)
            1 -> BarChart(context)
            else -> LineChart(context)
        }
        chart.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ChartViewHolder(chart)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        when (val chart = holder.itemView) {
            is PieChart -> {
                pieChartRef = chart // 💾 Сохраняем при биндинге
                bindPieChart(chart)
            }
            is BarChart -> bindBarChart(chart)
            is LineChart -> bindLineChart(chart)
        }
    }

    class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun bindPieChart(pieChart: PieChart) {
        val dataSet = PieDataSet(data, "Категории")
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = android.graphics.Color.WHITE

        pieChart.data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK)
        pieChart.setCenterText("Всего\n$totalAmount ₽")
        pieChart.setCenterTextSize(18f)
        pieChart.setHoleRadius(40f)
        pieChart.setTransparentCircleRadius(45f)

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    onCategoryClick(e.label)
                }
            }

            override fun onNothingSelected() {}
        })

        val desc = Description()
        desc.text = "Круговая диаграмма"
        pieChart.description = desc
        pieChart.invalidate()
    }

    private fun bindBarChart(barChart: BarChart) {
        val entries = data.mapIndexed { index, pieEntry ->
            BarEntry(index.toFloat(), pieEntry.value)
        }
        val labels = data.map { it.label }

        val dataSet = BarDataSet(entries, "Категории")
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS.toList())
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barChart.data = barData

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f
        barChart.description.text = "Столбчатая диаграмма"
        barChart.invalidate()
    }

    private fun bindLineChart(lineChart: LineChart) {
        val entries = data.mapIndexed { index, pieEntry ->
            Entry(index.toFloat(), pieEntry.value)
        }
        val dataSet = LineDataSet(entries, "Категории")
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
        dataSet.setCircleColor(android.graphics.Color.BLACK)
        dataSet.valueTextSize = 14f

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.description.text = "Биржевая диаграмма"
        lineChart.invalidate()
    }
}