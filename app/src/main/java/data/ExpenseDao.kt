package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense) // 🔄 Обновление

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :fromDate ORDER BY date DESC")
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses WHERE date >= :fromDate GROUP BY category")
    fun getCategoryTotalsFromDate(fromDate: Long): LiveData<List<CategoryTotal>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun getById(id: Int): LiveData<Expense> // 🔍 Получение по ID

    // ✅ Новое: Получить все расходы по категории
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>
}