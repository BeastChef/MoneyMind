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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends AppCompatActivity {

    private Spinner statsFilterSpinner;
    private RadioGroup statsTypeGroup;
    private ViewPager2 chartViewPager;
    private TabLayout chartTabLayout;
    private FloatingActionButton fabBack;
    private ChartPagerAdapter chartPagerAdapter;

    private int selectedStatsType = R.id.statsTypeAll;
    private int selectedStatsPeriod = 0;

    private ExpenseViewModel viewModel;
    private LiveData<List<Expense>> currentExpenses;
    private Observer<List<Expense>> expenseObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();
        setupChartPager();
        setupSpinnerAndFilters();
        setupFab();
    }

    private void initViews() {
        statsFilterSpinner = findViewById(R.id.statsFilterSpinner);
        statsTypeGroup = findViewById(R.id.statsTypeGroup);
        chartViewPager = findViewById(R.id.chartViewPager);
        chartTabLayout = findViewById(R.id.chartTabLayout);
        fabBack = findViewById(R.id.fabBackToMain);

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

    private void setupSpinnerAndFilters() {
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
                selectedStatsPeriod = position;
                updateStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        statsTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedStatsType = checkedId;
            updateStats();
        });

        updateStats(); // сразу загрузить данные
    }

    private void setupFab() {
        fabBack.setOnClickListener(v -> finish());
    }

    private void updateStats() {
        LiveData<List<Expense>> selectedExpenses = getSelectedExpenses();
        observeExpenses(selectedExpenses);
    }

    private LiveData<List<Expense>> getSelectedExpenses() {
        boolean isIncome = selectedStatsType == R.id.statsTypeIncomes;
        boolean isExpense = selectedStatsType == R.id.statsTypeExpenses;

        switch (selectedStatsPeriod) {
            case 1: return isIncome ? viewModel.getLast7DaysIncomes()
                    : isExpense ? viewModel.getLast7DaysExpensesOnly()
                    : viewModel.getLast7DaysExpenses();
            case 2: return isIncome ? viewModel.getLast30DaysIncomes()
                    : isExpense ? viewModel.getLast30DaysExpensesOnly()
                    : viewModel.getLast30DaysExpenses();
            case 3: return isIncome ? viewModel.getLast90DaysIncomes()
                    : isExpense ? viewModel.getLast90DaysExpensesOnly()
                    : viewModel.getLast90DaysExpenses();
            case 4: return isIncome ? viewModel.getLast365DaysIncomes()
                    : isExpense ? viewModel.getLast365DaysExpensesOnly()
                    : viewModel.getLast365DaysExpenses();
            default: return isIncome ? viewModel.getAllIncomes()
                    : isExpense ? viewModel.getAllExpensesOnly()
                    : viewModel.getExpenses();
        }
    }

    private void observeExpenses(LiveData<List<Expense>> expensesLiveData) {
        if (currentExpenses != null && expenseObserver != null) {
            currentExpenses.removeObserver(expenseObserver);
        }

        if (expensesLiveData == null) return;

        currentExpenses = expensesLiveData;

        expenseObserver = expenses -> {
            if (expenses != null) {
                boolean isSummaryMode = (selectedStatsType == R.id.statsTypeAll);
                chartPagerAdapter.setExpenses(expenses, isSummaryMode);
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