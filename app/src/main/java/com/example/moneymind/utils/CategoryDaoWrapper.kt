package com.example.moneymind.utils

import android.content.Context
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CategoryDaoWrapper(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val categoryDao: CategoryDao = db.categoryDao()

    // Метод для асинхронной очистки всех категорий
    fun deleteAllCategories() {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.deleteAll() // Удаляем все категории
        }
    }

    // Метод для асинхронного добавления всех категорий
    fun insertCategories(categories: List<Category>) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insertAll(categories) // Добавляем категории
        }
    }

    // Метод для асинхронного обновления всех категорий
    fun updateCategories(categories: List<Category>) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.updateAll(categories) // Обновляем категории
        }
    }

}