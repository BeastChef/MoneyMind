package com.example.moneymind.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymind.R;

public class ChooseTypeActivity extends BaseActivityJ  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

        findViewById(R.id.blockIncome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTypeActivity.this, ChooseIncomeCategoryActivity.class);
                intent.putExtra("CATEGORY_TYPE", "income"); // ✅ передаём тип
                startActivity(intent);
            }
        });

        findViewById(R.id.blockExpense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTypeActivity.this, ChooseExpenseCategoryActivity.class);
                intent.putExtra("CATEGORY_TYPE", "expense"); // ✅ передаём тип
                startActivity(intent);
            }
        });
    }
}