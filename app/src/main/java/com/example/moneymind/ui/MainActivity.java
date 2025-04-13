package com.example.moneymind.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymind.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔽 Подключаем адаптер
        RecyclerView recyclerView = findViewById(R.id.expensesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Expense> expenses = Arrays.asList(
                new Expense("Мясо", "-500 ₽", "10 апр 2025", android.R.drawable.ic_menu_info_details),
                new Expense("Мёд", "-200 ₽", "11 апр 2025", android.R.drawable.ic_menu_info_details),
                new Expense("Пиво викингов", "-300 ₽", "12 апр 2025", android.R.drawable.ic_menu_info_details)
        );

        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        recyclerView.setAdapter(adapter);
    }
}