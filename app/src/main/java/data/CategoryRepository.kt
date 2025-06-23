package com.example.moneymind.data

import androidx.lifecycle.LiveData
import com.example.moneymind.model.CustomCategoryEntity

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val customCategoryDao: CustomCategoryDao // ✅ Добавлен DAO кастомных категорий
) {

    // ---------- Обычные категории ----------
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }

    suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getById(id)
    }

    suspend fun insertAll(categories: List<Category>) {
        categories.forEach { insert(it) }
    }

    fun getExpenseCategories(): LiveData<List<Category>> {
        return categoryDao.getExpenseCategories()
    }

    fun getIncomeCategories(): LiveData<List<Category>> {
        return categoryDao.getIncomeCategories()
    }

    fun getCategoriesByType(isIncome: Boolean): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(isIncome)
    }

    // ---------- Кастомные категории ----------
    suspend fun insertCustom(category: CustomCategoryEntity) {
        customCategoryDao.insert(category)
    }

    suspend fun updateCustom(category: CustomCategoryEntity) {
        customCategoryDao.update(category)
    }

    suspend fun deleteCustomById(id: Int) {
        customCategoryDao.deleteById(id)
    }

    fun getCustomCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>> {
        return customCategoryDao.getCategories(isIncome)
    }
}