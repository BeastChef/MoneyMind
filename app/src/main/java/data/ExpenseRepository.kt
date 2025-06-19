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

    // ✅ Метод для поиска по названию
    fun searchExpensesByTitle(query: String): LiveData<List<Expense>> {
        return expenseDao.searchByTitle("%$query%")
    }

    // ✅ Метод: получить расходы по точной дате (с 00:00 до 23:59)
    fun getExpensesByExactDate(date: Long): LiveData<List<Expense>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val end = calendar.timeInMillis - 1
        return expenseDao.getByExactDateRange(start, end)
    }

    // ✅ Метод: получить транзакции в интервале между двумя датами
    fun getExpensesBetweenDates(start: Long, end: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(start, end)
    }

    // ✅ Вставка, обновление, удаление
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}