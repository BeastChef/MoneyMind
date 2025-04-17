package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :fromDate ORDER BY date DESC")
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>
}