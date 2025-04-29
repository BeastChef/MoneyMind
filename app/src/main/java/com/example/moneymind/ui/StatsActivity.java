package com.example.moneymind.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.data.Expense;
import com.example.moneymind.ui.charts.ChartPagerAdapter;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends AppCompatActivity {

    private Spinner statsFilterSpinner;
    private RadioGroup statsTypeGroup;
    private ViewPager2 chartViewPager;
    private TabLayout chartTabLayout;
    private FloatingActionButton fabBack;
    private FloatingActionButton fabPickDate;
    private ChartPagerAdapter chartPagerAdapter;

    private int selectedStatsType = R.id.statsTypeAll;
    private int selectedPeriod = 0; // 0 = 7 дней, 1 = 30 дней
    private ExpenseViewModel viewModel;
    private LiveData<List<Expense>> currentExpenses;
    private Observer<List<Expense>> expenseObserver;

    private long customStartDate = 0;
    private long customEndDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();
        setupChartPager();
        setupActions();
        setupSpinner();
        updateStats(); // сразу по умолчанию 7 дней
    }

    private void initViews() {
        statsFilterSpinner = findViewById(R.id.statsFilterSpinner);
        statsTypeGroup = findViewById(R.id.statsTypeGroup);
        chartViewPager = findViewById(R.id.chartViewPager);
        chartTabLayout = findViewById(R.id.chartTabLayout);
        fabBack = findViewById(R.id.fabBackToMain);
        fabPickDate = findViewById(R.id.fabPickDate);

        viewModel = new ViewModelProvider(
                this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository())
        ).get(ExpenseViewModel.class);
    }

    private void setupChartPager() {
        chartPagerAdapter = new ChartPagerAdapter(this, clickedLabel -> {
            showDetailsDialog(clickedLabel);
            return null;
        });
        chartViewPager.setAdapter(chartPagerAdapter);

        new TabLayoutMediator(chartTabLayout, chartViewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Круговая"); break;
                case 1: tab.setText("Столбцы"); break;
                case 2: tab.setText("Биржевая"); break;
            }
        }).attach();
    }

    private void setupActions() {
        fabBack.setOnClickListener(v -> finish());
        fabPickDate.setOnClickListener(v -> openDatePicker());

        statsTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedStatsType = checkedId;
            updateStats();
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"7 дней", "30 дней"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statsFilterSpinner.setAdapter(adapter);

        statsFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPeriod = position;
                customStartDate = 0; // сброс кастомного выбора
                customEndDate = 0;
                updateStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void openDatePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Выберите период");

        MaterialDatePicker<?> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection instanceof androidx.core.util.Pair) {
                androidx.core.util.Pair<Long, Long> range = (androidx.core.util.Pair<Long, Long>) selection;
                customStartDate = range.first;
                customEndDate = range.second;
                updateStats();
            }
        });
    }

    private void updateStats() {
        LiveData<List<Expense>> selectedExpenses;

        if (customStartDate != 0 && customEndDate != 0) {
            selectedExpenses = viewModel.getExpensesBetween(customStartDate, customEndDate);
        } else {
            long now = System.currentTimeMillis();
            long daysAgo = selectedPeriod == 0 ? 7 : 30;
            long fromDate = now - daysAgo * 24L * 60 * 60 * 1000;
            selectedExpenses = viewModel.getExpensesBetween(fromDate, now);
        }

        observeExpenses(selectedExpenses);
    }

    private void observeExpenses(LiveData<List<Expense>> expensesLiveData) {
        if (currentExpenses != null && expenseObserver != null) {
            currentExpenses.removeObserver(expenseObserver);
        }

        currentExpenses = expensesLiveData;

        expenseObserver = expenses -> {
            if (expenses != null) {
                boolean isSummaryMode = (selectedStatsType == R.id.statsTypeAll);
                chartPagerAdapter.setExpenses(expenses, isSummaryMode, selectedStatsType);
            }
        };

        currentExpenses.observe(this, expenseObserver);
    }

    private void showDetailsDialog(String label) {
        if (currentExpenses == null || currentExpenses.getValue() == null) return;

        List<Expense> expenses = currentExpenses.getValue();
        List<String> details = new ArrayList<>();
        SimpleDateFormat sdfShort = new SimpleDateFormat("dd MMM", Locale.getDefault());

        for (Expense exp : expenses) {
            String shortDate = sdfShort.format(new Date(exp.getDate()));
            if (shortDate.equals(label)) {
                String prefix = exp.getType().equals("income") ? "+ " : "- ";
                details.add("• " + exp.getTitle() + " — " + prefix + exp.getAmount() + " ₽");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(label)
                .setMessage(details.isEmpty() ? "Нет данных за этот день" : String.join("\n", details))
                .setPositiveButton("ОК", null)
                .show();
    }
}