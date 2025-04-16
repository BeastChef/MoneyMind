package com.example.moneymind.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    fun getCategoryTotals(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotals()
    }
}