package com.example.moneymind.ui;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.moneymind.utils.LocaleHelper;

public class BaseActivityJ extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString("app_lang", "ru");
        Context context = LocaleHelper.INSTANCE.setLocale(newBase, lang);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        applySavedTheme(); // Устанавливаем тему до вызова super.onCreate
        super.onCreate(savedInstanceState);
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int themePref = prefs.getInt("app_theme", 2); // 0 = светлая, 1 = тёмная, 2 = системная

        switch (themePref) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}