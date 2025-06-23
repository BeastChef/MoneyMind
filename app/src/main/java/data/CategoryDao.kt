package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {

    // Получить все категории (LiveData)
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // Получить все категории (немедленно — для использования из MainActivity и т.д.)
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllNow(): List<Category> // 👈 обычный метод, не suspend, не LiveData

    // Получить только расходные категории
    @Query("SELECT * FROM categories WHERE is_income = 0 ORDER BY name ASC")
    fun getExpenseCategories(): LiveData<List<Category>>

    // Получить только доходные категории
    @Query("SELECT * FROM categories WHERE is_income = 1 ORDER BY name ASC")
    fun getIncomeCategories(): LiveData<List<Category>>

    // Получить категории по типу
    @Query("SELECT * FROM categories WHERE is_income = :isIncome ORDER BY name ASC")
    fun getCategoriesByType(isIncome: Boolean): LiveData<List<Category>>

    // Добавить одну категорию
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    // Добавить список категорий
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<Category>) // 👈 важно для добавления по умолчанию

    // Удалить категорию
    @Delete
    suspend fun delete(category: Category)
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    fun getByIdSync(id: Int): Category?


    // Получить категорию по ID
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Category?

}