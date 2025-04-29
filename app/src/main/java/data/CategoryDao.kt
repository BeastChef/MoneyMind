package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {

    // Получить все категории
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // Добавить новую категорию
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    // Удалить категорию
    @Delete
    suspend fun delete(category: Category)

    // 🔥 Новый метод — получить категорию по id
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Category?
}