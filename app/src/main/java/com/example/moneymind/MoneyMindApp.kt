package com.example.moneymind

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.data.ExpenseRepository
import com.example.moneymind.utils.DefaultCategoryInitializer
import com.example.moneymind.utils.LocaleHelper

class MoneyMindApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: ExpenseRepository by lazy {
        ExpenseRepository(database.expenseDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(
            database.categoryDao(),
            database.customCategoryDao()
        )
    }

    override fun onCreate() {
        super.onCreate()

        // 🌍 Устанавливаем язык один раз
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("app_lang", "ru") ?: "ru"
        LocaleHelper.setLocale(this, lang)

        // 🎨 Устанавливаем тему
        val themePref = prefs.getInt("app_theme", 2)
        when (themePref) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // ✅ Инициализируем категории (без дублей, с учетом языка)
        DefaultCategoryInitializer.initAsync(this)
    }
}