package com.example.moneymind.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.example.moneymind.R;
import com.example.moneymind.data.AppDatabase;
import com.example.moneymind.data.Category;
import com.example.moneymind.data.CategoryRepository;
import com.example.moneymind.data.Expense;
import com.example.moneymind.data.ExpenseRepository;
import com.example.moneymind.utils.DefaultCategoryInitializer;
import com.example.moneymind.utils.FirestoreHelper;
import com.example.moneymind.utils.LocaleHelper;
import com.example.moneymind.utils.OnSwipeTouchListener;
import com.example.moneymind.viewmodel.CategoryViewModel;
import com.example.moneymind.viewmodel.CategoryViewModelFactory;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import java.util.concurrent.Executors;

public class MainActivity extends BaseActivityJ {
    private ExpenseViewModel expenseViewModel;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1002;
    private static final int REQUEST_CHOOSE_CATEGORY = 1001;

    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;
    private TextView accountStatusText;
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

    private CategoryViewModel categoryViewModel;
    private FirestoreHelper firestoreHelper;

    private long customStartDate = 0;
    private long customEndDate = 0;
    private boolean customRangeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

// Настройка GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Токен из Firebase Console
                .requestEmail()
                .build();

// Инициализация GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 🧠 Вот здесь вызываем обновление категорий



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Инициализация FirebaseAuth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser(); // Проверяем, авторизован ли пользователь
        // ✅ ViewModel
        ExpenseRepository expenseRepository = new ExpenseRepository(AppDatabase.getDatabase(this).expenseDao());
        CategoryRepository categoryRepository = new CategoryRepository(AppDatabase.getDatabase(this).categoryDao(), AppDatabase.getDatabase(this).customCategoryDao());
        ExpenseViewModelFactory factory = new ExpenseViewModelFactory( new ExpenseRepository(AppDatabase.getDatabase(this).expenseDao()),
                new CategoryRepository(AppDatabase.getDatabase(this).categoryDao(), AppDatabase.getDatabase(this).customCategoryDao()),
                getApplication() // Передаем application
        );
        expenseViewModel = new ViewModelProvider(this, factory).get(ExpenseViewModel.class);
        viewModel = expenseViewModel;  // 🔥 Делаем так, чтобы viewModel не была null

        CategoryViewModelFactory categoryFactory = new CategoryViewModelFactory(categoryRepository);
        categoryViewModel = new ViewModelProvider(this, categoryFactory).get(CategoryViewModel.class);

        if (currentUser == null) {
            // Пользователь не авторизован, продолжаем в режиме гостя
            setupUI(); // Инициализация UI для гостевого пользователя
        } else {
            // Пользователь авторизован, выполняем синхронизацию данных с Firebase
            initializeEverything(); // Синхронизация данных
        }

        // Слушатели и UI элементы
        setupUI(); // Инициализация UI, которая теперь также зависит от состояния пользователя

    }
    private void setupUI() {

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
                openDatePicker();  // Здесь вызываем метод для открытия календаря
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

        adapter.setOnExpenseClickListener(null); // Убираем обработчик кликов

        adapter.setOnExpenseLongClickListener(expense -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_record_message))
                    .setMessage(getString(R.string.delete_record_message) + " «" + expense.getCategory() + "»?")
                    .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                        viewModel.delete(expense);  // Удаление записи
                        Log.d("Delete", "Expense deleted: " + expense.getCategory());
                        updateFilteredData();  // Пересчитываем фильтрацию после удаления
                    })
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

// Находим кнопку для входа через Google внутри headerView
        Button googleSignInButton = navView.findViewById(R.id.googleSignInButton);
        accountStatusText = navView.findViewById(R.id.accountStatusText);
        updateAccountStatus(accountStatusText);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.isAnonymous()) {
                accountStatusText.setText(" ");
            } else {
                accountStatusText.setText(getString(R.string.logged_in_as, user.getDisplayName()));
            }
        }
        Button logoutButton = navView.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                Toast.makeText(this, " ", Toast.LENGTH_SHORT).show();
                                updateAccountStatus(accountStatusText); // 🟢 обновляем текст
                                recreate(); // 🔄 перезапуск для обновления UI
                            } else {
                                Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        });

        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

// Проверка на null и установка других кнопок в навигационном меню (для языка и темы)
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
    // Метод синхронизации данных с Firestore
    private void initializeEverything() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (!document.exists()) {
                            // 🔥 Новый пользователь — создаём дефолтные категории

                        }

                        // После этого синкаем категории
                        expenseViewModel.syncCategoriesFromFirestore(success -> {
                            if (success) {
                                Log.d("Sync", "Категории успешно синхронизированы");
                            } else {
                                Log.e("Sync", "Ошибка при синхронизации категорий");
                            }
                            return null;
                        });

                        // И расходы
                        expenseViewModel.syncExpensesFromFirestore(success -> {
                            if (success) {
                                Log.d("Sync", "Расходы успешно синхронизированы");
                            } else {
                                Log.e("Sync", "Ошибка при синхронизации расходов");
                            }
                            return null;
                        });
                    });
        }
    }
    private void synchronizeData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return; // Пользователь не авторизован
        }

// Сохраняем данные пользователя в Firestore (email, баланс, категории)
        FirestoreHelper.saveUserDataToFirestore(currentUser.getUid(), currentUser.getEmail());  // Данные о пользователе

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = currentUser.getUid();

        // Загружаем расходы из Firestore
        db.collection("users").document(userId).collection("expenses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Expense expense = document.toObject(Expense.class);
                        // Сохраняем данные в локальную базу
                        expenseViewModel.insertExpense(expense);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading expenses", e);
                });

        // Загружаем категории из Firestore
        db.collection("users").document(userId).collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        // Сохраняем категории в локальную базу
                        expenseViewModel.insertCategory(category);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading categories", e);
                });
    }

    private void openDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText(getString(R.string.select_period_title));  // Устанавливаем заголовок для выбора периода

        // Создаем и показываем MaterialDatePicker
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        // Пользователь выбрал диапазон дат
        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                customStartDate = selection.first;
                customEndDate = selection.second;
                customRangeActive = true;  // Включаем пользовательский диапазон дат
                updateFilteredData();  // Обновляем данные с учётом выбранных дат
            }
        });

        // Пользователь отменил выбор
        picker.addOnNegativeButtonClickListener(dialog -> {
            customRangeActive = false;  // Отключаем пользовательский диапазон
            updateFilteredData();  // Обновляем данные с учётом сброса фильтра
        });

        // Пользователь закрыл окно, не выбрав даты
        picker.addOnCancelListener(dialog -> {
            customRangeActive = false;  // Сбрасываем флаг
            updateFilteredData();  // Обновляем данные с учётом сброса фильтра
        });
    }


    private LiveData<List<Expense>> currentData;

    private void updateFilteredData() {
        Log.d("FilteredData", "Update started.");

        boolean isExpense = selectedTypeFilter == R.id.filterExpenses;
        boolean isIncome = selectedTypeFilter == R.id.filterIncomes;

        // Новый параметр для фильтрации по типу (расходы/доходы)
        String typeFilter = isExpense ? "expense" : isIncome ? "income" : "all"; // фильтрация по типу

        LiveData<List<Expense>> newData;

        if (customRangeActive) {
            newData = viewModel.getExpensesBetweenDates(customStartDate, customEndDate, typeFilter);
        } else {
            switch (selectedDateFilter) {
                case 1:
                    newData = isExpense ? viewModel.getLast7DaysExpensesOnly()
                            : isIncome ? viewModel.getLast7DaysIncomes()
                            : viewModel.getLast7DaysAll();
                    break;
                case 2:
                    newData = isExpense ? viewModel.getLast30DaysExpensesOnly()
                            : isIncome ? viewModel.getLast30DaysIncomes()
                            : viewModel.getLast30DaysAll();
                    break;
                default:
                    newData = isExpense ? viewModel.getAllExpensesOnly()
                            : isIncome ? viewModel.getAllIncomes()
                            : viewModel.getAllExpenses();
            }
        }

        // Отвязываем предыдущий observer
        if (currentData != null) {
            currentData.removeObservers(this);
        }
        currentData = newData;

        // Подписываемся только один раз
        currentData.observe(this, expenses -> {
            Log.d("FilteredData", "Filtered expenses count after update: " + expenses.size());
            adapter.setExpenseList(expenses);  // Обновляем список с использованием метода адаптера
            updateSummaryCards(expenses);  // Обновляем итоговые данные
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
                .setTitle(getString(R.string.search))
                .setPositiveButton(getString(R.string.search_button), (dialog, which) -> {
                    String query = input.getText().toString().trim();
                    if (!query.isEmpty()) {
                        viewModel.searchExpensesByTitleOrCategory(query).observe(this, expenses -> {
                            if (expenses == null || expenses.isEmpty()) {
                                adapter.setExpenseList(List.of());
                                Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.setExpenseList(expenses);
                                updateSummaryCards(expenses);
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> updateFilteredData())
                .show();
    }

    private void showThemeDialog() {
        String[] themes = {
                getString(R.string.light_theme),
                getString(R.string.dark_theme),

        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.change_theme))
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

        });

        // Перезапускаем активити
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
        finish();
    }
    private void updateAccountStatus(TextView accountStatusText) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.isAnonymous()) {
                accountStatusText.setText("Вы вошли как: Гость");
            } else {
                accountStatusText.setText(getString(R.string.logged_in_as, user.getDisplayName()));
            }
        } else {
            accountStatusText.setText(getString(R.string.guest_mode));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential)
                            .addOnCompleteListener(this, task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, getString(R.string.google_signin_success), Toast.LENGTH_SHORT).show();
                                    updateAccountStatus(accountStatusText);

                                    // Сохраняем данные пользователя в Firestore
                                    FirestoreHelper.saveUserDataToFirestore(user.getUid(), user.getEmail());

                                    // Восстанавливаем данные из предыдущего аккаунта (гостевого)
                                    String fromUid = "guest"; // Можно также использовать UID гостевого пользователя
                                    FirestoreHelper.checkAndRestoreData(fromUid); // Восстанавливаем данные

                                    // Загружаем данные пользователя из Firestore после восстановления
                                    expenseViewModel.restoreFromFirebase();  // Загрузить данные для расходов
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.google_signin_failed), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, getString(R.string.google_signin_error) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        // Обработка результата выбора категории (оставь как есть)
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