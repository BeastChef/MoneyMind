package com.example.moneymind.data

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // 🔹 Все записи (доходы и расходы)
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    // 🔹 Только расходы
    val allExpensesOnly: LiveData<List<Expense>> = expenseDao.getAllExpensesOnly()

    // 🔹 Только доходы
    val allIncomes: LiveData<List<Expense>> = expenseDao.getAllIncomes()
    fun getAllFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getAllTransactionsFromDate(fromDate)
    }


    // 🔽 Все типы (доходы и расходы) по дате
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesFromDate(fromDate)
    }

    // 🔽 Только расходы по дате
    fun getExpensesFromDateOnly(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesFromDateOnly(fromDate)
    }

    // 🔽 Только доходы по дате
    fun getIncomesFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getIncomesFromDate(fromDate)
    }

    // 🔽 Категории — все
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotals()
    }

    // 🔽 Категории — только расходы
    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotalsOnly()
    }

    // 🔽 Категории — только расходы по дате
    fun getCategoryTotalsFromDateOnly(fromDate: Long): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotalsFromDateOnly(fromDate)
    }

    // 🔍 Один по ID
    fun getExpenseById(id: Int): LiveData<Expense> {
        return expenseDao.getById(id)
    }

    // 🔍 Все по категории
    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }

    // 💾 Добавить
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    // ♻️ Обновить
    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    // ❌ Удалить
    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}