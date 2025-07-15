package com.example.moneymind.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.data.Expense;
import com.example.moneymind.utils.TooltipBlocker;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends BaseActivityJ {

    private Spinner statsFilterSpinner;
    private RadioGroup statsTypeGroup;
    private ViewPager2 chartViewPager;
    private TabLayout chartTabLayout;
    private ChartPagerAdapter chartPagerAdapter;

    private int selectedStatsType = R.id.statsTypeAll;
    private int selectedPeriod = 0;

    private ExpenseViewModel viewModel;
    private LiveData<List<Expense>> currentExpenses;
    private Observer<List<Expense>> expenseObserver;

    private long customStartDate = 0;
    private long customEndDate = 0;

    private AlertDialog activeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        TooltipBlocker.INSTANCE.disableAllTooltips(findViewById(android.R.id.content));

        Toolbar toolbar = findViewById(R.id.statsToolbar);
        setSupportActionBar(toolbar);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, android.R.color.white));
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        disableTooltips();
        setupChartPager();
        setupActions();
        setupSpinner();
        updateStats();
    }

    private void initViews() {
        statsFilterSpinner = findViewById(R.id.statsFilterSpinner);
        statsTypeGroup = findViewById(R.id.statsTypeGroup);
        chartViewPager = findViewById(R.id.chartViewPager);
        chartTabLayout = findViewById(R.id.chartTabLayout);

        viewModel = new ViewModelProvider(
                this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository())
        ).get(ExpenseViewModel.class);
    }

    private void disableTooltips() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            statsFilterSpinner.setTooltipText(null);
            findViewById(R.id.statsTypeAll).setTooltipText(null);
            findViewById(R.id.statsTypeIncomes).setTooltipText(null);
            findViewById(R.id.statsTypeExpenses).setTooltipText(null);
            chartTabLayout.setTooltipText(null);
            chartViewPager.setTooltipText(null);
        }
    }

    private void setupChartPager() {
        chartPagerAdapter = new ChartPagerAdapter(this, clickedLabel -> {
            showDetailsDialog(clickedLabel);
            return null;
        });
        chartViewPager.setAdapter(chartPagerAdapter);

        new TabLayoutMediator(chartTabLayout, chartViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.chart_pie));
                    break;
                case 1:
                    tab.setText(getString(R.string.chart_bar));
                    break;
                case 2:
                    tab.setText(getString(R.string.chart_candlestick));
                    break;
            }
        }).attach();
    }

    private void setupActions() {
        statsTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedStatsType = checkedId;
            updateStats();
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{
                        getString(R.string.stats_filter_month),
                        getString(R.string.stats_filter_year)
                }
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statsFilterSpinner.setAdapter(adapter);

        statsFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedPeriod = position;
                customStartDate = 0;
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

        // Заголовок локализован
        builder.setTitleText(getString(R.string.select_period_title));

        // Строим и показываем диалог
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
            long fromDate;

            Calendar cal = Calendar.getInstance();
            if (selectedPeriod == 0) {
                cal.add(Calendar.MONTH, -1);
            } else {
                cal.add(Calendar.YEAR, -1);
            }
            fromDate = cal.getTimeInMillis();

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
                details.add(exp.getCategory()+". " + exp.getTitle() +" : " + prefix + exp.getAmount());
            }
        }

        activeDialog = new AlertDialog.Builder(this)
                .setTitle(label)
                .setMessage(details.isEmpty()
                        ? getString(R.string.no_data_for_selected_day)
                        : String.join("\n", details))
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (activeDialog != null && activeDialog.isShowing()) {
            activeDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_pick_date) {
            openDatePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}