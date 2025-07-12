package com.example.moneymind.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.data.AppDatabase;
import com.example.moneymind.data.Category;
import com.example.moneymind.data.Expense;
import com.example.moneymind.utils.DefaultCategoryInitializer;
import com.example.moneymind.utils.LocaleHelper;
import com.example.moneymind.utils.OnSwipeTouchListener;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivityJ {

    private static final int REQUEST_CHOOSE_CATEGORY = 1001;

    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;

    private TextView incomeAmountText, expenseAmountText, balanceAmountText;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout typeSelectionContainer;
    private Button btnChooseIncome, btnChooseExpense;
    private MaterialToolbar topAppBar;
    private Spinner filterSpinner;
    private RadioGroup typeFilterGroup;
    private int selectedDateFilter = 0;
    private int selectedTypeFilter = R.id.filterAll;

    private long customStartDate = 0;
    private long customEndDate = 0;
    private boolean customRangeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🧠 Вот здесь вызываем обновление категорий
        DefaultCategoryInitializer.INSTANCE.updateCategoriesIfNeeded(this);
        DefaultCategoryInitializer.INSTANCE.updateNamesAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository()))
                .get(ExpenseViewModel.class);

        topAppBar = findViewById(R.id.topAppBar);
        filterSpinner = findViewById(R.id.filterSpinner);
        typeFilterGroup = findViewById(R.id.typeFilterGroup);
        incomeAmountText = findViewById(R.id.incomeAmount);
        expenseAmountText = findViewById(R.id.expenseAmount);
        balanceAmountText = findViewById(R.id.balanceAmount);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        typeSelectionContainer = findViewById(R.id.typeSelectionContainer);
        btnChooseIncome = findViewById(R.id.btnChooseIncome);
        btnChooseExpense = findViewById(R.id.btnChooseExpense);

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_calendar) {
                openDatePicker();
                return true;
            } else if (item.getItemId() == R.id.action_search) {
                showSearchDialog();
                return true;
            }
            return false;
        });

        String[] filterOptions = getResources().getStringArray(R.array.filter_options);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                filterOptions
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedDateFilter = pos;
                customRangeActive = false;
                updateFilteredData();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        typeFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedTypeFilter = checkedId;
            updateFilteredData();
        });

        RecyclerView recyclerView = findViewById(R.id.expensesRecyclerView);
        adapter = new ExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(new LayoutAnimationController(
                AnimationUtils.loadAnimation(this, R.anim.item_animation)));

        adapter.setOnExpenseClickListener(expense -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            intent.putExtra("expense_id", expense.getId());
            intent.putExtra("selected_category", expense.getCategory());
            intent.putExtra("selected_icon", expense.getIconName());
            intent.putExtra("is_income", "income".equals(expense.getType()));
            intent.putExtra("is_custom", true);
            intent.putExtra("from_main_tab", true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        adapter.setOnExpenseLongClickListener(expense -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_record_message))  // Здесь будет перевод "Удалить запись"
                    .setMessage(getString(R.string.delete_record_message) + " «" + expense.getCategory() + "»?")
                    .setPositiveButton(getString(R.string.delete), (dialog, which) -> viewModel.delete(expense))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });

        findViewById(R.id.fabAddExpense).setOnClickListener(v -> {
            if (typeSelectionContainer.getVisibility() == View.GONE) {
                typeSelectionContainer.setAlpha(0f);
                typeSelectionContainer.setVisibility(View.VISIBLE);
                typeSelectionContainer.animate().alpha(1f).setDuration(300).start();
            } else {
                typeSelectionContainer.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> typeSelectionContainer.setVisibility(View.GONE))
                        .start();
            }
        });

        btnChooseIncome.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseIncomeCategoryActivity.class);
            intent.putExtra("button_text", getString(R.string.choose_category)); // Передаем строку для кнопки
            startActivityForResult(intent, REQUEST_CHOOSE_CATEGORY);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


        btnChooseExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseExpenseCategoryActivity.class);
            startActivityForResult(intent, REQUEST_CHOOSE_CATEGORY);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        findViewById(R.id.btnStats).setOnClickListener(v -> {
            startActivity(new Intent(this, StatsActivity.class));
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });

        View navView = navigationView.getHeaderView(0);
        if (navView == null && navigationView.getChildCount() > 0)
            navView = navigationView.getChildAt(0);

        if (navView != null) {
            View btnLang = navView.findViewById(R.id.btnChangeLanguage);
            if (btnLang != null) {
                btnLang.setOnClickListener(v -> {
                    String[] langs = getResources().getStringArray(R.array.language_names);
                    String[] codes = getResources().getStringArray(R.array.language_codes);

                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.language_dialog_title))
                            .setItems(langs, (dialog, which) -> setLocale(codes[which]))
                            .show();
                });
            }

            View btnTheme = navView.findViewById(R.id.btnChangeTheme);
            if (btnTheme != null) {
                btnTheme.setOnClickListener(v -> showThemeDialog());
            }
        }

        findViewById(R.id.main).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override public void onSwipeRight() {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        updateFilteredData();
    }

    private void openDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText(getString(R.string.select_period_title));

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        // ✅ Пользователь нажал OK
        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                customStartDate = selection.first;
                customEndDate = selection.second;
                customRangeActive = true;
                updateFilteredData();
            }
        });

        // ✅ Пользователь нажал "Отмена"
        picker.addOnNegativeButtonClickListener(dialog -> {
            customRangeActive = false;
            updateFilteredData(); // ← сбрасываем кастомный фильтр
        });

        // ✅ Пользователь тапнул вне календаря
        picker.addOnCancelListener(dialog -> {
            customRangeActive = false;
            updateFilteredData(); // ← сбрасываем кастомный фильтр
        });
    }

    private void updateFilteredData() {
        LiveData<List<Expense>> data;
        boolean isExpense = selectedTypeFilter == R.id.filterExpenses;
        boolean isIncome = selectedTypeFilter == R.id.filterIncomes;

        if (customRangeActive) {
            data = viewModel.getExpensesBetweenDates(customStartDate, customEndDate);
        } else {
            switch (selectedDateFilter) {
                case 1:
                    data = isExpense ? viewModel.getLast7DaysExpensesOnly()
                            : isIncome ? viewModel.getLast7DaysIncomes()
                            : viewModel.getLast7DaysAll();
                    break;
                case 2:
                    data = isExpense ? viewModel.getLast30DaysExpensesOnly()
                            : isIncome ? viewModel.getLast30DaysIncomes()
                            : viewModel.getLast30DaysAll();
                    break;
                default:
                    data = isExpense ? viewModel.getAllExpensesOnly()
                            : isIncome ? viewModel.getAllIncomes()
                            : viewModel.getAllExpenses();
            }
        }

        data.observe(this, expenses -> {
            adapter.setExpenseList(expenses);
            updateSummaryCards(expenses);
        });
    }

    private void updateSummaryCards(List<Expense> expenses) {
        double income = 0, expense = 0;
        for (Expense e : expenses) {
            if ("income".equals(e.getType())) income += e.getAmount();
            else expense += e.getAmount();
        }
        double balance = income - expense;

        incomeAmountText.setText(String.valueOf(income));
        expenseAmountText.setText(String.valueOf(expense));
        balanceAmountText.setText(String.valueOf(balance));
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        EditText input = dialogView.findViewById(R.id.editSearchInput);

        builder.setView(dialogView)
                .setTitle(getString(R.string.search))  // Получаем строку из ресурсов
                .setPositiveButton(getString(R.string.search_button), (dialog, which) -> {
                    String query = input.getText().toString().trim();
                    if (!query.isEmpty()) {
                        viewModel.searchExpensesByTitleOrCategory(query).observe(this, expenses -> {
                            if (expenses == null || expenses.isEmpty()) {
                                adapter.setExpenseList(List.of()); // Пустой список
                                Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.setExpenseList(expenses);
                                updateSummaryCards(expenses);
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    updateFilteredData(); // Сброс — возврат к фильтрации по времени
                })
                .show();
    }

    private void showThemeDialog() {
        String[] themes = {
                getString(R.string.light_theme),
                getString(R.string.dark_theme)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_theme))  // Получаем строку из ресурсов
                .setItems(themes, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        case 1:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case 2:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                    }
                    getSharedPreferences("settings", MODE_PRIVATE).edit().putInt("app_theme", which).apply();
                    recreate();
                })
                .show();
    }

    private void setLocale(String langCode) {
        getSharedPreferences("settings", MODE_PRIVATE)
                .edit().putString("app_lang", langCode).apply();

        // Обновляем язык UI
        LocaleHelper.setLocale(this, langCode);

        // Обновляем названия категорий асинхронно
        Executors.newSingleThreadExecutor().execute(() -> {
            DefaultCategoryInitializer.INSTANCE.updateNamesAsync(getApplicationContext());
        });

        // Перезапускаем активити
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_CATEGORY && resultCode == RESULT_OK && data != null) {
            int categoryId = data.getIntExtra("selected_category_id", -1);
            if (categoryId != -1) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    Category category = AppDatabase.getDatabase(getApplicationContext())
                            .categoryDao().getByIdSync(categoryId);
                    if (category != null) {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                            intent.putExtra("selected_category", category.getName());
                            intent.putExtra("is_income", category.isIncome());
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        });
                    }
                });
            }
        }
    }
}