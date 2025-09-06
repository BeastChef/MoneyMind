package com.example.moneymind.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.data.Expense
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ChartPagerAdapter(
    private val context: Context,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<ChartPagerAdapter.ChartViewHolder>() {

    private var expenseData: List<Expense> = emptyList()
    private var summaryMode: Boolean = false
    private var selectedStatsType: Int = 0

    // Метод для проверки текущей темы (темная или светлая)
    private fun isDarkMode(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    fun setExpenses(expenses: List<Expense>, isSummaryMode: Boolean, statsType: Int) {
        expenseData = expenses.sortedBy { it.date }
        summaryMode = isSummaryMode
        selectedStatsType = statsType
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
            is PieChart -> setupPieChart(chart)
            is BarChart -> bindBarChart(chart)
            is LineChart -> bindLineChart(chart)
        }
    }

    class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun bindBarChart(barChart: BarChart) {
        val (entries, labels) = prepareChartData()

        val dataSet = BarDataSet(entries, "Баланс по дням").apply {
            colors = entries.map {
                when (selectedStatsType) {
                    R.id.statsTypeIncomes -> ContextCompat.getColor(context, R.color.income_color_strong)
                    R.id.statsTypeExpenses -> ContextCompat.getColor(context, R.color.expense_color_orange)
                    else -> if (it.y >= 0)
                        ContextCompat.getColor(context, R.color.income_color_strong)
                    else
                        ContextCompat.getColor(context, R.color.expense_color_orange)
                }
            }
            valueTextSize = 12f
            valueTextColor = if (isDarkMode()) Color.WHITE else Color.BLACK  // Белый для темной темы, черный для светлой
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
        val (barEntries, labels) = prepareChartData()
        val entries = barEntries.map { Entry(it.x, it.y) }

        val lineColor = when (selectedStatsType) {
            R.id.statsTypeIncomes -> ContextCompat.getColor(context, R.color.income_color_strong)
            R.id.statsTypeExpenses -> ContextCompat.getColor(context, R.color.expense_color_orange)
            else -> if (isDarkMode()) Color.WHITE else Color.BLACK  // Белый для темной темы, черный для светлой
        }

        val dataSet = LineDataSet(entries, "Баланс по дням").apply {
            color = lineColor
            setCircleColor(lineColor)
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 12f
            valueTextColor = if (isDarkMode()) Color.WHITE else Color.BLACK  // Белый для темной темы, черный для светлой
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

    private fun setupPieChart(chart: PieChart) {
        val entries = mutableListOf<PieEntry>()
        val labels = mutableListOf<String>()
        val colors = mutableListOf<Int>()

        // Изменяем цвет центрального круга в зависимости от темы
        val centerCircleColor = if (isDarkMode()) Color.parseColor("#424242") else Color.WHITE  // Темно-серый для темной темы, белый для светлой

        when (selectedStatsType) {
            R.id.statsTypeAll -> {
                val incomeLabel = context.getString(R.string.filter_incomes)
                val expenseLabel = context.getString(R.string.filter_expenses)
                val centerText = context.getString(R.string.finance_chart)

                val incomeTotal = expenseData.filter { it.type == "income" }.sumOf { it.amount }.toFloat()
                val expenseTotal = expenseData.filter { it.type == "expense" }.sumOf { it.amount }.toFloat()

                if (incomeTotal > 0f) {
                    entries.add(PieEntry(incomeTotal, incomeLabel))
                    labels.add(incomeLabel)
                    colors.add(ContextCompat.getColor(context, R.color.income_color_strong))
                }

                if (expenseTotal > 0f) {
                    entries.add(PieEntry(expenseTotal, expenseLabel))
                    labels.add(expenseLabel)
                    colors.add(ContextCompat.getColor(context, R.color.expense_color_orange))
                }

                applyPieChart(chart, entries, labels, colors, centerText, expenseData, true, centerCircleColor)
            }

            R.id.statsTypeIncomes, R.id.statsTypeExpenses -> {
                val isIncome = selectedStatsType == R.id.statsTypeIncomes
                val filtered = expenseData.filter { it.type == if (isIncome) "income" else "expense" }

                val grouped = filtered.groupBy { it.category }

                for ((category, expenses) in grouped) {
                    val sum = expenses.sumOf { it.amount }.toFloat()
                    if (sum > 0f) {
                        entries.add(PieEntry(sum, category))
                        labels.add(category)

                        // берем цвет из первой записи этой категории
                        val color = expenses.firstOrNull()?.categoryColor ?: Color.LTGRAY
                        colors.add(color)
                    }
                }

                val centerText = if (isIncome)
                    context.getString(R.string.filter_incomes)
                else
                    context.getString(R.string.filter_expenses)

                applyPieChart(chart, entries, labels, colors, centerText, filtered, false, centerCircleColor)
            }
        }

        // Цвет для текста и значений на PieChart
        chart.setEntryLabelColor(if (isDarkMode()) Color.WHITE else Color.BLACK)
        chart.setCenterTextColor(if (isDarkMode()) Color.WHITE else Color.BLACK)
    }

    private fun applyPieChart(
        chart: PieChart,
        entries: List<PieEntry>,
        labels: List<String>,
        colors: List<Int>,
        centerText: String,
        dataSource: List<Expense>,
        summaryMode: Boolean,
        centerCircleColor: Int
    ) {
        val incomeStr = context.getString(R.string.filter_incomes)
        val expenseStr = context.getString(R.string.filter_expenses)

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = if (isDarkMode()) Color.WHITE else Color.BLACK

        val data = PieData(dataSet)
        chart.data = data
        chart.centerText = centerText
        chart.setEntryLabelColor(Color.DKGRAY)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setCenterTextSize(18f)
        chart.setHoleRadius(40f)
        chart.setTransparentCircleRadius(45f)
        chart.setHoleColor(centerCircleColor)  // Устанавливаем цвет центрального круга

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val label = (e as? PieEntry)?.label ?: return
                val filtered = if (summaryMode) {
                    val type = if (label == incomeStr) "income" else "expense"
                    dataSource.filter { it.type == type }
                } else {
                    dataSource.filter { it.category == label }
                }

                val details = filtered.map {
                    val sign = if (it.type == "income") "+ " else "- "
                    "• ${it.category}. ${it.title} : $sign${it.amount} "
                }

                AlertDialog.Builder(context)
                    .setTitle(label)
                    .setMessage(details.joinToString("\n").ifEmpty { context.getString(R.string.no_data_for_selected_day) })
                    .setPositiveButton(context.getString(R.string.ok), null)
                    .show()
            }

            override fun onNothingSelected() {}
        })

        chart.animateY(800)
        chart.invalidate()
    }



    private fun applyPieChart(
        chart: PieChart,
        entries: List<PieEntry>,
        labels: List<String>,
        colors: List<Int>,
        centerText: String,
        dataSource: List<Expense>,
        summaryMode: Boolean
    ) {
        val incomeStr = context.getString(R.string.filter_incomes)
        val expenseStr = context.getString(R.string.filter_expenses)

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = if (isDarkMode()) Color.WHITE else Color.BLACK

        val data = PieData(dataSet)
        chart.data = data
        chart.centerText = centerText
        chart.setEntryLabelColor(Color.DKGRAY)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setCenterTextSize(18f)
        chart.setHoleRadius(40f)
        chart.setTransparentCircleRadius(45f)

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val label = (e as? PieEntry)?.label ?: return
                val filtered = if (summaryMode) {
                    val type = if (label == incomeStr) "income" else "expense"
                    dataSource.filter { it.type == type }
                } else {
                    dataSource.filter { it.category == label }
                }

                val details = filtered.map {
                    val sign = if (it.type == "income") "+ " else "- "
                    "• ${it.category}. ${it.title} : $sign${it.amount} "
                }

                AlertDialog.Builder(context)
                    .setTitle(label)
                    .setMessage(details.joinToString("\n").ifEmpty { context.getString(R.string.no_data_for_selected_day) })
                    .setPositiveButton(context.getString(R.string.ok), null)
                    .show()
            }

            override fun onNothingSelected() {}
        })

        chart.animateY(800)
        chart.invalidate()
    }

    private fun prepareChartData(): Pair<List<BarEntry>, List<String>> {
        val dateChanges = mutableMapOf<String, Float>()

        for (expense in expenseData) {
            val date = expense.getShortDate()
            val amount = expense.amount.toFloat()

            when (selectedStatsType) {
                R.id.statsTypeIncomes -> if (expense.type == "income")
                    dateChanges[date] = (dateChanges[date] ?: 0f) + amount
                R.id.statsTypeExpenses -> if (expense.type == "expense")
                    dateChanges[date] = (dateChanges[date] ?: 0f) - amount
                else -> {
                    if (expense.type == "income") {
                        dateChanges[date] = (dateChanges[date] ?: 0f) + amount
                    } else if (expense.type == "expense") {
                        dateChanges[date] = (dateChanges[date] ?: 0f) - amount
                    }
                }
            }
        }

        val sortedDates = dateChanges.keys.sortedBy { parseDate(it) }
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var cumulativeSum = 0f
        sortedDates.forEachIndexed { index, date ->
            cumulativeSum += dateChanges[date] ?: 0f
            entries.add(BarEntry(index.toFloat(), cumulativeSum))
            labels.add(date)
        }

        return Pair(entries, labels)
    }

    private fun setupChartBase(chart: BarLineChartBase<*>, labels: List<String>) {
        // Для светлой или темной темы
        val textColor = if (isDarkMode()) Color.WHITE else Color.BLACK

        // Настройка оси X
        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            labelRotationAngle = 45f
            setTextColor(textColor)  // Используем метод setTextColor для изменения цвета текста оси X
        }

        // Настройка оси Y (левая ось)
        chart.axisLeft.apply {
            axisMinimum = -findMaxY()
            axisMaximum = findMaxY()
            setDrawGridLines(true)
            setTextColor(textColor)  // Используем метод setTextColor для изменения цвета текста оси Y
        }

        // Настройка правой оси (отключение)
        chart.axisRight.isEnabled = false

        // Отключаем ненужные элементы
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
    }

    private fun findMaxY(): Float {
        val balances = prepareChartData().first.map { abs(it.y) }
        return (balances.maxOrNull() ?: 0f) * 1.2f
    }

    private fun Expense.getShortDate(): String {
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return sdf.format(Date(this.date))
    }

    private fun parseDate(dateStr: String): Date {
        return SimpleDateFormat("dd MMM", Locale.getDefault()).parse(dateStr) ?: Date()
    }
}