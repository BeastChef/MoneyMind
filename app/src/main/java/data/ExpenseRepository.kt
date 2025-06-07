package com.example.moneymind.data

import androidx.lifecycle.LiveData
import java.util.Calendar

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

    // 🔍 Поиск по названию
    fun searchExpensesByTitle(query: String): LiveData<List<Expense>> {
        return expenseDao.searchExpensesByTitle("%$query%")
    }

    // 📅 Получение расходов по точной дате
    fun getExpensesByDate(date: Long): LiveData<List<Expense>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return expenseDao.getExpensesByExactDate(start, end)
    }

    // 📆 Получение расходов между двумя датами (нужно для графиков)
    fun getExpensesBetweenDates(start: Long, end: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(start, end)
    }
}