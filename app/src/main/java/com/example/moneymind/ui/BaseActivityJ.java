package com.example.moneymind.ui;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moneymind.utils.LocaleHelper;

public class BaseActivityJ extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString("app_lang", "ru");
        Context context = LocaleHelper.INSTANCE.setLocale(newBase, lang);
        super.attachBaseContext(context);
    }
}