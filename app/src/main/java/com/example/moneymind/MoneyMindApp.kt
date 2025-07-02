package com.example.moneymind

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.data.ExpenseRepository
import com.example.moneymind.utils.LocaleHelper
import com.example.moneymind.worker.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

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

    override fun attachBaseContext(base: Context?) {
        val prefs = base?.getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs?.getString("app_lang", "ru") ?: "ru"
        val updatedContext = LocaleHelper.setLocale(base!!, lang)
        super.attachBaseContext(updatedContext)

    }

    override fun onCreate() {
        super.onCreate()

        // 🌍 Установка языка
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("app_lang", "ru") ?: "ru"
        LocaleHelper.setLocale(this, lang)

        // 🎨 Установка темы
        val themePref = prefs.getInt("app_theme", 2) // 0 - светлая, 1 - тёмная, 2 - системная
        when (themePref) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // ❗Если нужно запланировать уведомление — раскомментируй строку ниже:
        // scheduleDailyReminder()
    }

    private fun scheduleDailyReminder() {
        val now = Calendar.getInstance()
        val target = now.clone() as Calendar
        target.set(Calendar.HOUR_OF_DAY, 20)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("expense_reminder")
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "daily_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}