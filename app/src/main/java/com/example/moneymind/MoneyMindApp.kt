package com.example.moneymind

import android.app.Application
import androidx.work.*
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.ExpenseRepository
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

    override fun onCreate() {
        super.onCreate()
        scheduleDailyReminder()
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