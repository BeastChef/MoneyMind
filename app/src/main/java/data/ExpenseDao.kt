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

    // 🔽 Все записи (доходы и расходы)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Expense>>

    // ✅ 🔽 Метод для репозитория — все записи (все типы)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>  // ⬅ добавлен метод

    // ✅ 🔽 Метод для репозитория — все записи с фильтром по дате
    @Query("SELECT * FROM transactions WHERE date >= :fromDate ORDER BY date DESC")
    fun getExpensesFromDate(fromDate: Long): LiveData<List<Expense>>  // ⬅ добавлен метод

    // ✅ Только расходы
    @Query("SELECT * FROM transactions WHERE type = 'expense' ORDER BY date DESC")
    fun getAllExpensesOnly(): LiveData<List<Expense>>

    // ✅ Только доходы
    @Query("SELECT * FROM transactions WHERE type = 'income' ORDER BY date DESC")
    fun getAllIncomes(): LiveData<List<Expense>>

    // ✅ Только расходы с даты
    @Query("SELECT * FROM transactions WHERE date >= :fromDate AND type = 'expense' ORDER BY date DESC")
    fun getExpensesFromDateOnly(fromDate: Long): LiveData<List<Expense>>

    // ✅ Только доходы с даты
    @Query("SELECT * FROM transactions WHERE date >= :fromDate AND type = 'income' ORDER BY date DESC")
    fun getIncomesFromDate(fromDate: Long): LiveData<List<Expense>>

    // ✅ Категории (все записи)
    @Query("SELECT category, SUM(amount) AS total FROM transactions GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    // ✅ Только расходы (категории)
    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE type = 'expense' GROUP BY category")
    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>>

    // ✅ Категории всех с фильтрацией по дате
    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE date >= :fromDate GROUP BY category")
    fun getCategoryTotalsFromDate(fromDate: Long): LiveData<List<CategoryTotal>>

    // ✅ Только расходы с датой
    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE date >= :fromDate AND type = 'expense' GROUP BY category")
    fun getCategoryTotalsFromDateOnly(fromDate: Long): LiveData<List<CategoryTotal>>

    // 🔍 Получение по ID
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): LiveData<Expense>

    // 🔍 По категории
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>
    @Query("SELECT * FROM transactions WHERE date >= :fromDate ORDER BY date DESC")
    fun getAllTransactionsFromDate(fromDate: Long): LiveData<List<Expense>>
}