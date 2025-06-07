package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE date >= :fromDate ORDER BY date DESC")
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE type = 'expense' ORDER BY date DESC")
    fun getAllExpensesOnly(): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE type = 'income' ORDER BY date DESC")
    fun getAllIncomes(): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE date >= :fromDate AND type = 'expense' ORDER BY date DESC")
    fun getExpensesFromDateOnly(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE date >= :fromDate AND type = 'income' ORDER BY date DESC")
    fun getIncomesFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT category, SUM(amount) AS total FROM transactions GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE type = 'expense' GROUP BY category")
    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE date >= :fromDate GROUP BY category")
    fun getCategoryTotalsFromDate(fromDate: Long): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE date >= :fromDate AND type = 'expense' GROUP BY category")
    fun getCategoryTotalsFromDateOnly(fromDate: Long): LiveData<List<CategoryTotal>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): LiveData<Expense>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE date >= :fromDate ORDER BY date DESC")
    fun getAllTransactionsFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getExpensesBetweenDates(start: Long, end: Long): LiveData<List<Expense>>

    // 🔍 Поиск по названию
    @Query("SELECT * FROM transactions WHERE title LIKE :query ORDER BY date DESC")
    fun searchExpensesByTitle(query: String): LiveData<List<Expense>>

    // 📅 Получить записи по точной дате (с 00:00 до 23:59)
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesByExactDate(start: Long, end: Long): LiveData<List<Expense>>
}