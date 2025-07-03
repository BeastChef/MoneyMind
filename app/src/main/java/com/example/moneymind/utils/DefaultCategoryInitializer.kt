package com.example.moneymind.utils

import android.content.Context
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DefaultCategoryInitializer {

    @JvmStatic
    fun initAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val categoryDao = db.categoryDao()

            val existingIcons = categoryDao.getAllNow().map { it.iconName }.toSet()
            val res = context.resources

            val defaultCategories = getDefaultCategories(res)

            defaultCategories
                .filterNot { existingIcons.contains(it.iconName) }
                .forEach { categoryDao.insert(it) }
        }
    }

    @JvmStatic
    fun updateNamesAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val dao = db.categoryDao()
            val categories = dao.getAllNow()
            val res = context.resources

            val updated = categories.map { category ->
                when (category.iconName) {
                    "ic_salary" -> category.copy(name = res.getString(R.string.category_salary))
                    "ic_investments" -> category.copy(name = res.getString(R.string.category_investments))
                    "ic_gift" -> category.copy(name = res.getString(R.string.category_gift))
                    "ic_food" -> category.copy(name = res.getString(R.string.category_food))
                    "ic_transport" -> category.copy(name = res.getString(R.string.category_transport))
                    "ic_medical" -> category.copy(name = res.getString(R.string.category_medical))
                    "ic_shopping" -> category.copy(name = res.getString(R.string.category_shopping))
                    "ic_myhome" -> category.copy(name = res.getString(R.string.category_home))
                    "ic_entertainment" -> category.copy(name = res.getString(R.string.category_entertainment))
                    else -> category
                }
            }

            dao.updateAll(updated)
        }
    }

    @JvmStatic
    fun updateCategoriesIfNeeded(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val dao = db.categoryDao()
            val existingIcons = dao.getAllNow().map { it.iconName }.toSet()

            val categoriesToInsert = getDefaultCategories(context.resources)
                .filterNot { existingIcons.contains(it.iconName) }

            dao.insertAll(categoriesToInsert)
        }
    }

    private fun getDefaultCategories(res: android.content.res.Resources): List<Category> {
        return listOf(
            // Доходы
            Category(0, res.getString(R.string.category_salary), "ic_salary", R.drawable.ic_salary, true),
            Category(0, res.getString(R.string.category_investments), "ic_investments", R.drawable.ic_investments, true),
            Category(0, res.getString(R.string.category_gift), "ic_gift", R.drawable.ic_gift, true),

            // Расходы
            Category(0, res.getString(R.string.category_food), "ic_food", R.drawable.ic_food, false),
            Category(0, res.getString(R.string.category_transport), "ic_transport", R.drawable.ic_transport, false),
            Category(0, res.getString(R.string.category_medical), "ic_medical", R.drawable.ic_medical, false),
            Category(0, res.getString(R.string.category_shopping), "ic_shopping", R.drawable.ic_shopping, false),
            Category(0, res.getString(R.string.category_home), "ic_myhome", R.drawable.ic_myhome, false),
            Category(0, res.getString(R.string.category_gift), "ic_gift", R.drawable.ic_gift, false),
            Category(0, res.getString(R.string.category_entertainment), "ic_entertainment", R.drawable.ic_entertainment, false)
        )
    }
}