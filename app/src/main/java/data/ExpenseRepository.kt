package com.example.moneymind.data

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Все расходы
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    // Получить расходы с определённой даты
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesFromDate(fromDate)
    }

    // Получить категории с суммами
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotals()
    }

    // Получить один расход по ID
    fun getExpenseById(id: Int): LiveData<Expense> {
        return expenseDao.getById(id)
    }

    // Добавить новый расход
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    // Обновить существующий расход
    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    // Удалить расход
    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}