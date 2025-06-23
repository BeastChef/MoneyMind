package com.example.moneymind.utils

import android.content.Context
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DefaultCategoryInitializer {

    @JvmStatic // 👈 позволяет вызвать из Java
    fun initAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val categoryDao = db.categoryDao()

            val current = categoryDao.getAllNow()
            if (current.isEmpty()) {
                val expenses = DefaultCategoriesProvider.getDefaultExpenseCategories()
                val incomes = DefaultCategoriesProvider.getDefaultIncomeCategories()

                expenses.forEach { categoryDao.insert(it) }
                incomes.forEach { categoryDao.insert(it) }
            }
        }
    }
}