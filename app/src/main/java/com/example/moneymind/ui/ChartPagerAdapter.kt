package com.example.moneymind.ui.charts

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.data.Expense
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ChartPagerAdapter(
    private val context: Context,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<ChartPagerAdapter.ChartViewHolder>() {

    private var pieData: List<PieEntry> = emptyList()
    private var expenseData: List<Expense> = emptyList()
    private var summaryMode: Boolean = false

    fun setChartData(entries: List<PieEntry>) {
        pieData = entries
        notifyDataSetChanged()
    }

    fun setExpenses(expenses: List<Expense>, isSummaryMode: Boolean = false) {
        expenseData = expenses.sortedBy { it.date }
        summaryMode = isSummaryMode
        notifyDataSetChanged()
    }

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
            is PieChart -> bindPieChart(chart)
            is BarChart -> bindBarChart(chart)
            is LineChart -> bindLineChart(chart)
        }
    }

    class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun bindPieChart(pieChart: PieChart) {
        val dataSet = PieDataSet(pieData, "Категории")
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        pieChart.data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "Расходы/Доходы"
        pieChart.setCenterTextSize(18f)
        pieChart.setHoleRadius(40f)
        pieChart.setTransparentCircleRadius(45f)

        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    onCategoryClick(e.label)
                }
            }
            override fun onNothingSelected() {}
        })

        pieChart.animateY(800)
        pieChart.invalidate()
    }

    private fun bindBarChart(barChart: BarChart) {
        val (entries, labels) = prepareBarChartData()

        val dataSet = BarDataSet(entries, "Дни").apply {
            colors = entries.map { if (it.y >= 0) Color.GREEN else Color.RED }
            valueTextSize = 12f
            valueTextColor = Color.BLACK
        }

        barChart.data = BarData(dataSet).apply { barWidth = 0.8f }
        barChart.setFitBars(true)

        setupChartBase(barChart, labels)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = e?.x?.toInt() ?: return
                if (index in labels.indices) {
                    onCategoryClick(labels[index])
                }
            }
            override fun onNothingSelected() {}
        })

        barChart.animateY(800)
        barChart.invalidate()
    }

    private fun bindLineChart(lineChart: LineChart) {
        val (entries, labels) = prepareLineChartData()

        val dataSet = LineDataSet(entries, "Дни").apply {
            color = ColorTemplate.getHoloBlue()
            setCircleColor(ColorTemplate.getHoloBlue())
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 12f
        }

        lineChart.data = LineData(dataSet)

        setupChartBase(lineChart, labels)

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = e?.x?.toInt() ?: return
                if (index in labels.indices) {
                    onCategoryClick(labels[index])
                }
            }
            override fun onNothingSelected() {}
        })

        lineChart.animateX(800)
        lineChart.invalidate()
    }

    private fun prepareBarChartData(): Pair<List<BarEntry>, List<String>> {
        val dateSums = aggregateData()
        val sortedDates = dateSums.keys.sortedBy { parseDate(it) }
        val entries = sortedDates.mapIndexed { index, date -> BarEntry(index.toFloat(), dateSums[date] ?: 0f) }
        val labels = sortedDates
        return Pair(entries, labels)
    }

    private fun prepareLineChartData(): Pair<List<Entry>, List<String>> {
        val dateSums = aggregateData()
        val sortedDates = dateSums.keys.sortedBy { parseDate(it) }
        val entries = sortedDates.mapIndexed { index, date -> Entry(index.toFloat(), dateSums[date] ?: 0f) }
        val labels = sortedDates
        return Pair(entries, labels)
    }

    private fun aggregateData(): Map<String, Float> {
        val dateSums = mutableMapOf<String, Float>()
        for (expense in expenseData) {
            val date = expense.getShortDate()
            val value = when {
                summaryMode -> if (expense.type == "income") expense.amount.toFloat() else -expense.amount.toFloat()
                else -> abs(expense.amount.toFloat())
            }
            dateSums[date] = (dateSums[date] ?: 0f) + value
        }
        return dateSums
    }

    private fun setupChartBase(chart: BarLineChartBase<*>, labels: List<String>) {
        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            labelRotationAngle = 45f
        }

        chart.axisLeft.apply {
            axisMinimum = -findMaxY()
            axisMaximum = findMaxY()
            setDrawGridLines(true)
        }

        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
    }

    private fun findMaxY(): Float {
        return (expenseData.maxOfOrNull { abs(it.amount.toFloat()) } ?: 0f) * 1.2f
    }

    private fun Expense.getShortDate(): String {
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return sdf.format(Date(this.date))
    }

    private fun parseDate(dateStr: String): Date {
        return SimpleDateFormat("dd MMM", Locale.getDefault()).parse(dateStr) ?: Date()
    }
}