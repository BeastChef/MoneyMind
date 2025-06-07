package com.example.moneymind.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymind.R;

public class ChooseTypeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

        findViewById(R.id.blockIncome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseTypeActivity.this, ChooseIncomeCategoryActivity.class));
            }
        });

        findViewById(R.id.blockExpense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseTypeActivity.this, ChooseExpenseCategoryActivity.class));
            }
        });
    }
}