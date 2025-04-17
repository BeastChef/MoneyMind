package com.example.moneymind.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymind.MoneyMindApp;
import com.example.moneymind.R;
import com.example.moneymind.viewmodel.ExpenseViewModel;
import com.example.moneymind.viewmodel.ExpenseViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🔐 Разрешения для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }

        // 🔲 Отступы под системные панели
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔽 Список расходов
        RecyclerView recyclerView = findViewById(R.id.expensesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ExpenseAdapter adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        // ✨ Анимация карточек
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.item_animation);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView.setLayoutAnimation(controller);

        // 🔽 ViewModel + Repository
        ExpenseViewModel viewModel = new ViewModelProvider(
                this,
                new ExpenseViewModelFactory(((MoneyMindApp) getApplication()).getRepository())
        ).get(ExpenseViewModel.class);

        viewModel.getExpenses().observe(this, adapter::setExpenseList);

        // ➕ FAB: добавить расход
        findViewById(R.id.fabAddExpense).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        // 🧭 Обработка нижней навигации
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_stats) {
                startActivity(new Intent(this, StatsActivity.class));
                return true;
            }
            return false;
        });
    }
}