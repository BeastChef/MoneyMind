package com.example.moneymind.utils

import android.content.Context
import android.content.res.Resources
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

object DefaultCategoryInitializer {

    private const val PREFS_NAME = "settings"
    private const val KEY_LAST_LANG = "last_default_lang"

    // Инициализация категорий при установке приложения
    @JvmStatic
    fun initAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastLang = prefs.getString(KEY_LAST_LANG, null)
            val currentLang = Locale.getDefault().language

            if (lastLang == null || lastLang != currentLang) {
                val db = AppDatabase.getDatabase(context)
                val dao = db.categoryDao()

                // Удаляем только дефолтные (по iconName)
                val defaultIconNames = getDefaultCategories(context.resources).map { it.iconName }.toSet()
                val toDelete = dao.getAllNow().filter { it.iconName in defaultIconNames }
                toDelete.forEach { dao.delete(it) }

                // Добавляем новые дефолтные категории на нужном языке
                dao.insertAll(getDefaultCategories(context.resources))

                // Сохраняем текущий язык
                prefs.edit().putString(KEY_LAST_LANG, currentLang).apply()
            }
        }
    }

    // Обновление категорий при изменении языка
    @JvmStatic
    fun updateCategoriesIfNeeded(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val dao = db.categoryDao()
            val existingCategories = dao.getAllNow()
            val res = context.resources
            val defaultCategories = getDefaultCategories(res)

            // Сравниваем и обновляем категории
            defaultCategories.forEach { newCategory ->
                val existingCategory = existingCategories.find { it.iconName == newCategory.iconName }
                if (existingCategory == null) {
                    dao.insert(newCategory)  // Если нет категории, добавляем новую
                } else if (existingCategory.name != newCategory.name) {
                    dao.update(existingCategory.copy(name = newCategory.name))  // Обновляем название категории
                }
            }
        }
    }

    // Обновление названий категорий
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

    private fun getDefaultCategories(res: Resources): List<Category> {
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