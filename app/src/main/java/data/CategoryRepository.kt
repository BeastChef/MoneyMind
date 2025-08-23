package com.example.moneymind.data

import androidx.lifecycle.LiveData
import com.example.moneymind.model.CustomCategoryEntity

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val customCategoryDao: CustomCategoryDao
) {

    // ---------- Обычные (дефолтные) категории ----------

    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }


    // Метод для получения категории по имени и иконке
    suspend fun getCategoryByNameAndIcon(name: String, iconName: String): Category? {
        return categoryDao.getCategoryByNameAndIcon(name, iconName)
    }

    fun getCategoriesByType(isIncome: Boolean): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(isIncome)
    }

    // ---------- Кастомные категории (CustomCategoryEntity) ----------

    suspend fun insertCustom(category: CustomCategoryEntity) {
        customCategoryDao.insert(category)
    }

    suspend fun updateCustom(category: CustomCategoryEntity) {
        customCategoryDao.update(category)
    }

    suspend fun deleteCustom(category: CustomCategoryEntity) {
        customCategoryDao.delete(category)
    }




    fun getCustomCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>> {
        return customCategoryDao.getCategories(isIncome)
    }

}