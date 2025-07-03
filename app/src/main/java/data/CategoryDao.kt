package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moneymind.data.Category

@Dao
interface CategoryDao {

    // Получить все категории
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // Получить все категории немедленно (для внутренних вызовов)
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllNow(): List<Category>

    // Получить категории по типу доход/расход
    @Query("SELECT * FROM categories WHERE is_income = :isIncome ORDER BY name ASC")
    fun getCategoriesByType(isIncome: Boolean): LiveData<List<Category>>

    // Доходы
    @Query("SELECT * FROM categories WHERE is_income = 1 ORDER BY name ASC")
    fun getIncomeCategories(): LiveData<List<Category>>

    // Расходы
    @Query("SELECT * FROM categories WHERE is_income = 0 ORDER BY name ASC")
    fun getExpenseCategories(): LiveData<List<Category>>

    // Вставка одной категории
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)
    // ✅ ДОБАВЬ ВОТ ЭТО:
    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    // Вставка всех категорий (например, по умолчанию)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<Category>)

    // Получить по ID
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Category?

    // Получить по ID синхронно (для использования в .launch)
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    fun getByIdSync(id: Int): Category?

    // Обновить категорию
    @Update
    suspend fun update(category: Category)

    // Удалить категорию по ID
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Удалить категорию полностью
    @Delete
    suspend fun delete(category: Category)
    @Update
    fun updateAll(categories: List<Category>)

    // Сколько всего категорий
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}