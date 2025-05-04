package com.example.moneymind.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneymind.model.CustomCategoryEntity

@Dao
interface CustomCategoryDao {
    @Insert
    suspend fun insert(category: CustomCategoryEntity)

    @Query("SELECT * FROM custom_categories WHERE isIncome = :isIncome")
    fun getCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>>
}