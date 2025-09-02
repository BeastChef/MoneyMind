package com.example.moneymind.utils

import android.content.Context
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.model.CustomCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomCategoryDaoWrapper(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val customDao = db.customCategoryDao()

    // массовая очистка
    fun deleteAllCustom() {
        GlobalScope.launch(Dispatchers.IO) {
            customDao.deleteAll()
        }
    }

    // массовая вставка
    fun insertCustomCategories(categories: List<CustomCategoryEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            customDao.insertAll(categories)
        }
    }

    // одиночные операции
    fun insertCustomCategory(category: CustomCategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customDao.insert(category)
        }
    }

    fun updateCustomCategory(category: CustomCategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customDao.update(category)
        }
    }

    fun deleteCustomCategory(category: CustomCategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            customDao.delete(category)
        }
    }
}