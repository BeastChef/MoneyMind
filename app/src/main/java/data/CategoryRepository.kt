package com.example.moneymind.data

import androidx.lifecycle.LiveData

class CategoryRepository(private val categoryDao: CategoryDao) {

    // Получить все категории
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    // Вставить новую категорию
    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    // Удалить категорию
    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }

    // Получить категорию по ID
    suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getById(id)
    }
    suspend fun insertAll(categories: List<Category>) {
        categories.forEach { insert(it) }
    }

    // 🔹 Получить только расходные категории
    fun getExpenseCategories(): LiveData<List<Category>> {
        return categoryDao.getExpenseCategories()
    }

    // 🔹 Получить только доходные категории
    fun getIncomeCategories(): LiveData<List<Category>> {
        return categoryDao.getIncomeCategories()
    }
    fun getCategoriesByType(isIncome: Boolean): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(isIncome)
    }
}