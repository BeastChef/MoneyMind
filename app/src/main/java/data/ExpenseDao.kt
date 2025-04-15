package com.example.moneymind.data

import androidx.room.*
import com.example.moneymind.data.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

}