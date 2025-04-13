package com.example.moneymind

import android.app.Application
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.ExpenseRepository

class MoneyMindApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.expenseDao()) }
}