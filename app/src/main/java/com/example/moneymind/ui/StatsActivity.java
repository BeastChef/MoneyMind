package com.example.moneymind.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.data.CategoryTotal;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ExpenseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        pieChart = findViewById(R.id.pieChart);
        setupPieChart();

        viewModel = new ViewModelProvider(
                this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository())
        ).get(ExpenseViewModel.class);

        viewModel.getCategoryTotals().observe(this, categoryTotals -> {
            List<PieEntry> entries = new ArrayList<>();
            for (CategoryTotal item : categoryTotals) {
                entries.add(new PieEntry((float) item.getTotal(), item.getCategory()));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Категории");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueTextSize(14f);
            data.setValueTextColor(Color.WHITE);

            pieChart.setData(data);
            pieChart.invalidate(); // обновить диаграмму
        });
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Категории расходов");
        pieChart.setCenterTextSize(18f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        Description description = new Description();
        description.setText("Статистика");
        pieChart.setDescription(description);
    }
}