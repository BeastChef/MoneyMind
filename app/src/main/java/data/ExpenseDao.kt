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

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :fromDate ORDER BY date DESC")
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE type = 'expense' ORDER BY date DESC")
    fun getAllExpensesOnly(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE type = 'income' ORDER BY date DESC")
    fun getAllIncomes(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :fromDate AND type = 'expense' ORDER BY date DESC")
    fun getExpensesFromDateOnly(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :fromDate AND type = 'income' ORDER BY date DESC")
    fun getIncomesFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses WHERE type = 'expense' GROUP BY category")
    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses WHERE date >= :fromDate GROUP BY category")
    fun getCategoryTotalsFromDate(fromDate: Long): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses WHERE date >= :fromDate AND type = 'expense' GROUP BY category")
    fun getCategoryTotalsFromDateOnly(fromDate: Long): LiveData<List<CategoryTotal>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun getById(id: Int): LiveData<Expense>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :fromDate ORDER BY date DESC")
    fun getAllTransactionsFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE title LIKE :query ORDER BY date DESC")
    fun searchByTitle(query: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getExpensesBetweenDates(start: Long, end: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getByExactDateRange(start: Long, end: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE title LIKE :query OR category LIKE :query ORDER BY date DESC")
    fun searchByTitleOrCategory(query: String): LiveData<List<Expense>>

    // Запрос для получения расходов по категории и диапазону дат
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate AND category = :category ORDER BY date DESC")
    fun getExpensesByDateAndCategory(startDate: Long, endDate: Long, category: String): LiveData<List<Expense>>
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate AND type = :type ORDER BY date DESC")
    fun getExpensesBetweenDates(startDate: Long, endDate: Long, type: String): LiveData<List<Expense>>
}