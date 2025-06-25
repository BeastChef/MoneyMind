package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moneymind.model.CustomCategoryEntity

@Dao
interface CustomCategoryDao {

    // Добавление категории
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CustomCategoryEntity)

    // Получение всех категорий по типу (доход / расход) — LiveData
    @Query("SELECT * FROM custom_categories WHERE isIncome = :isIncome")
    fun getCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>>

    // Получение всех категорий по типу (доход / расход) — прямо сейчас (для корутин, без LiveData)
    @Query("SELECT * FROM custom_categories WHERE isIncome = :isIncome")
    suspend fun getAllNow(isIncome: Boolean): List<CustomCategoryEntity>

    // Обновление категории
    @Update
    suspend fun update(category: CustomCategoryEntity)

    // Удаление категории по ID
    @Query("DELETE FROM custom_categories WHERE id = :id")
    suspend fun deleteById(id: Int)
}