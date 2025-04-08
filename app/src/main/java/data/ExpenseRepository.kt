package com.example.moneymind.data

import kotlinx.coroutines.flow.Flow
import com.example.moneymind.data.Expense
import com.example.moneymind.ExpenseDao

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}