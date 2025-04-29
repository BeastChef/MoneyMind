package com.example.moneymind.data

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()
    val allExpensesOnly: LiveData<List<Expense>> = expenseDao.getAllExpensesOnly()
    val allIncomes: LiveData<List<Expense>> = expenseDao.getAllIncomes()

    fun getAllFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getAllTransactionsFromDate(fromDate)
    }

    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesFromDate(fromDate)
    }

    fun getExpensesFromDateOnly(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesFromDateOnly(fromDate)
    }

    fun getIncomesFromDate(fromDate: Long): LiveData<List<Expense>> {
        return expenseDao.getIncomesFromDate(fromDate)
    }

    fun getCategoryTotals(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotals()
    }

    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotalsOnly()
    }

    fun getCategoryTotalsFromDateOnly(fromDate: Long): LiveData<List<CategoryTotal>> {
        return expenseDao.getCategoryTotalsFromDateOnly(fromDate)
    }

    fun getExpenseById(id: Int): LiveData<Expense> {
        return expenseDao.getById(id)
    }

    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    // 🔥 Новая функция для диапазона дат (от startDate до endDate)
    fun getExpensesBetweenDates(start: Long, end: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(start, end)
    }
}