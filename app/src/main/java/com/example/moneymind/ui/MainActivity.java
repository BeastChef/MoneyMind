package com.example.moneymind.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.moneymind.data.Category;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.data.AppDatabase;
import com.example.moneymind.data.CategoryDao;
import com.example.moneymind.data.CategoryRepository;
import com.example.moneymind.data.Expense;
import com.example.moneymind.utils.DefaultCategoriesProvider;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;
    private static final int REQUEST_CHOOSE_CATEGORY = 1001;

    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;
    private Spinner filterSpinner;
    private RadioGroup typeFilterGroup;
    private TextView balanceText;
    private int selectedDateFilter = 0;
    private int selectedTypeFilter = R.id.filterAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 👇 Добавляем дефолтные категории, если их нет
        insertDefaultsIfEmpty(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        filterSpinner = findViewById(R.id.filterSpinner);
        typeFilterGroup = findViewById(R.id.typeFilterGroup);
        balanceText = findViewById(R.id.balanceText);

        RecyclerView recyclerView = findViewById(R.id.expensesRecyclerView);
        adapter = new ExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.item_animation);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView.setLayoutAnimation(controller);

        viewModel = new ViewModelProvider(
                this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository())
        ).get(ExpenseViewModel.class);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDateFilter = position;
                updateFilteredData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        typeFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedTypeFilter = checkedId;
            updateFilteredData();
        });

        adapter.setOnExpenseClickListener(expense -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            intent.putExtra("expense_id", expense.getId());
            startActivity(intent);
        });

        adapter.setOnExpenseLongClickListener(expense -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_record))
                    .setMessage("Удалить «" + expense.getCategory() + "»?")
                    .setPositiveButton(getString(R.string.delete_record), (dialog, which) -> viewModel.delete(expense))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });

        findViewById(R.id.fabAddExpense).setOnClickListener(v -> {
            String[] options = {getString(R.string.add_expense), getString(R.string.add_income)};
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.choose_category))
                    .setItems(options, (dialog, which) -> {
                        Intent intent;
                        if (which == 1) {
                            intent = new Intent(MainActivity.this, ChooseIncomeCategoryActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, ChooseExpenseCategoryActivity.class);
                        }
                        startActivityForResult(intent, REQUEST_CHOOSE_CATEGORY);
                    })
                    .show();
        });

        findViewById(R.id.fabLanguage).setOnClickListener(v -> {
            String[] languages = {"Русский", "English", "中文", "العربية", "Español", "Deutsch", "Türkçe", "Italiano", "日本語", "한국어"};
            String[] codes = {"ru", "en", "zh", "ar", "es", "de", "tr", "it", "ja", "ko"};
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Выберите язык")
                    .setItems(languages, (dialog, which) -> {
                        setLocale(codes[which]);
                    })
                    .show();
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            if (itemId == R.id.nav_stats) {
                startActivity(new Intent(this, StatsActivity.class));
                return true;
            }
            return false;
        });

        updateFilteredData();
    }

    private void updateFilteredData() {
        LiveData<List<Expense>> data;

        boolean isExpenseOnly = selectedTypeFilter == R.id.filterExpenses;
        boolean isIncomeOnly = selectedTypeFilter == R.id.filterIncomes;

        switch (selectedDateFilter) {
            case 1:
                data = isExpenseOnly ? viewModel.getLast7DaysExpensesOnly()
                        : isIncomeOnly ? viewModel.getLast7DaysIncomes()
                        : viewModel.getLast7DaysAll();
                break;
            case 2:
                data = isExpenseOnly ? viewModel.getLast30DaysExpensesOnly()
                        : isIncomeOnly ? viewModel.getLast30DaysIncomes()
                        : viewModel.getLast30DaysAll();
                break;
            default:
                data = isExpenseOnly ? viewModel.getAllExpensesOnly()
                        : isIncomeOnly ? viewModel.getAllIncomes()
                        : viewModel.getAllExpenses();
        }

        data.observe(this, expenses -> {
            adapter.setExpenseList(expenses);
            updateBalanceInfo(expenses);
        });
    }

    private void updateBalanceInfo(List<Expense> expenses) {
        double income = 0;
        double expense = 0;

        for (Expense e : expenses) {
            if ("income".equals(e.getType())) income += e.getAmount();
            else expense += e.getAmount();
        }

        double balance = income - expense;
        String result = getString(R.string.filter_incomes) + ": " + income + " ₽   " +
                getString(R.string.filter_expenses) + ": " + expense + " ₽   " +
                "Баланс: " + balance + " ₽";
        balanceText.setText(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHOOSE_CATEGORY && resultCode == RESULT_OK && data != null) {
            int categoryId = data.getIntExtra("selected_category_id", -1);
            if (categoryId != -1) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    Category category = AppDatabase.getDatabase(getApplicationContext()).categoryDao().getByIdSync(categoryId);
                    if (category != null) {
                        final String categoryName = category.getName();
                        final boolean isIncome = category.isIncome();

                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                            intent.putExtra("selected_category", categoryName);
                            intent.putExtra("is_income", isIncome);
                            startActivity(intent);
                        });
                    }
                });
            }
        }

    }

    private void setLocale(String langCode) {
        Locale newLocale = new Locale(langCode);
        Locale.setDefault(newLocale);

        Configuration config = new Configuration();
        config.setLocale(newLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    // ✅ Метод для вставки дефолтных категорий
    private void insertDefaultsIfEmpty(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);
            CategoryDao dao = db.categoryDao();

            if (dao.getAllNow().isEmpty()) {
                CategoryRepository repository = new CategoryRepository(dao);
                DefaultCategoriesProvider.INSTANCE.getDefaultExpenseCategories();
                DefaultCategoriesProvider.INSTANCE.getDefaultIncomeCategories();

            }
        });
    }
}