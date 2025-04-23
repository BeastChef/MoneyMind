package com.example.moneymind.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.data.CategoryTotal;
import com.example.moneymind.data.Expense;
import com.example.moneymind.ui.charts.ChartPagerAdapter;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private Spinner statsFilterSpinner;
    private ExpenseViewModel viewModel;
    private ViewPager2 chartViewPager;
    private ChartPagerAdapter chartPagerAdapter;
    private TabLayout chartTabLayout;
    private LiveData<List<Expense>> currentExpenses;
    private Observer<List<Expense>> expenseObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        statsFilterSpinner = findViewById(R.id.statsFilterSpinner);
        chartViewPager = findViewById(R.id.chartViewPager);
        chartTabLayout = findViewById(R.id.chartTabLayout);

        chartPagerAdapter = new ChartPagerAdapter(this, category -> {
            if (currentExpenses != null) {
                currentExpenses.observe(this, expenses -> {
                    List<String> details = new ArrayList<>();
                    for (Expense exp : expenses) {
                        if (exp.getCategory().equals(category)) {
                            String title = exp.getNote() != null ? exp.getNote() : exp.getCategory();
                            details.add("• " + title + " — " + exp.getAmount() + " ₽");
                        }
                    }
                    if (details.isEmpty()) {
                        details.add("Нет данных по этой категории");
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("Расходы: " + category)
                            .setMessage(String.join("\n", details))
                            .setPositiveButton("Ок", null)
                            .show();
                });
            }
            return null;
        });

        chartViewPager.setAdapter(chartPagerAdapter);

        new TabLayoutMediator(chartTabLayout, chartViewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Круговая"); break;
                case 1: tab.setText("Столбцы"); break;
                case 2: tab.setText("Биржевая"); break;
                default: tab.setText("График"); break;
            }
        }).attach();

        viewModel = new ViewModelProvider(
                this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository())
        ).get(ExpenseViewModel.class);

        setupSpinner();
        observeAndRender(viewModel.getCategoryTotals(), viewModel.getExpenses());
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.stats_filter_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statsFilterSpinner.setAdapter(adapter);

        statsFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LiveData<List<CategoryTotal>> selectedData;
                LiveData<List<Expense>> selectedExpenses;

                switch (position) {
                    case 1:
                        selectedData = viewModel.getLast7DaysCategoryTotals();
                        selectedExpenses = viewModel.getLast7DaysExpenses();
                        break;
                    case 2:
                        selectedData = viewModel.getLast30DaysCategoryTotals();
                        selectedExpenses = viewModel.getLast30DaysExpenses();
                        break;
                    case 3:
                        selectedData = viewModel.getLast90DaysCategoryTotals();
                        selectedExpenses = viewModel.getLast90DaysExpenses();
                        break;
                    case 4:
                        selectedData = viewModel.getLast365DaysCategoryTotals();
                        selectedExpenses = viewModel.getLast365DaysExpenses();
                        break;
                    case 0:
                    default:
                        selectedData = viewModel.getCategoryTotals();
                        selectedExpenses = viewModel.getExpenses();
                        break;
                }

                observeAndRender(selectedData, selectedExpenses);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ничего не делать
            }
        });
    }

    private void observeAndRender(@NonNull LiveData<List<CategoryTotal>> data, @NonNull LiveData<List<Expense>> expenses) {
        if (currentExpenses != null && expenseObserver != null) {
            currentExpenses.removeObserver(expenseObserver);
        }

        currentExpenses = expenses;

        expenseObserver = expensesList -> {
            // можно использовать для логики в будущем
        };

        currentExpenses.observe(this, expenseObserver);

        data.observe(this, categoryTotals -> {
            List<PieEntry> entries = new ArrayList<>();
            float totalSum = 0f;

            for (CategoryTotal item : categoryTotals) {
                float amount = (float) item.getTotal();
                totalSum += amount;
                entries.add(new PieEntry(amount, item.getCategory()));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Категории");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(14f);
            dataSet.setSliceSpace(3f);

            PieData pieData = new PieData(dataSet);

            // Анимация и отображение
            chartPagerAdapter.setChartData(entries, totalSum);
            chartPagerAdapter.getPieChart().setData(pieData);
            chartPagerAdapter.getPieChart().setCenterText("Всего:\n" + totalSum + " ₽");
            chartPagerAdapter.getPieChart().animateY(1000);
            chartPagerAdapter.getPieChart().invalidate();
        });
    }
}