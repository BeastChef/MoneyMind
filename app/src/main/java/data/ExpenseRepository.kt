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







    fun getExpenseById(id: Int): LiveData<Expense> {
        return expenseDao.getById(id)
    }





    // ✅ Метод для поиска по названию или категории
    fun searchExpensesByTitleOrCategory(query: String): LiveData<List<Expense>> {
        return expenseDao.searchByTitleOrCategory("%$query%")
    }



    // ✅ Метод для получения транзакций в интервале между двумя датами
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