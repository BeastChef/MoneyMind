package com.example.moneymind.data

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Все расходы
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    // Расходы с определённой даты (для фильтрации по 7/30 дням)
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesFromDate(fromDate)
    }

    // Суммы по категориям (все расходы)
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotals()
    }

    // Суммы по категориям за последние N дней
    fun getCategoryTotalsFromDate(fromDate: Long): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotalsFromDate(fromDate)
    }

    // Получение одного расхода по ID
    fun getExpenseById(id: Int): LiveData<Expense> {
        return expenseDao.getById(id)
    }

    // ✅ Расходы по конкретной категории
    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }

    // Добавить расход
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    // Обновить расход
    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    // Удалить расход
    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}