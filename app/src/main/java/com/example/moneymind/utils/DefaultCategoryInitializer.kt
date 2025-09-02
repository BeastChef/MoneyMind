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

                // Получаем текущие категории
                val existingCategories = dao.getAllNow()

                // Получаем дефолтные категории для текущего языка
                val defaultCategories = getDefaultCategories(context.resources)

                // Обновляем или добавляем новые категории
                defaultCategories.forEach { newCategory ->
                    val existingCategory = existingCategories.find { it.iconName == newCategory.iconName }
                    if (existingCategory == null) {
                        dao.insert(newCategory)  // Если нет категории, добавляем новую
                    } else if (existingCategory.name != newCategory.name) {
                        dao.update(existingCategory.copy(name = newCategory.name))  // Обновляем название категории
                    }
                }

                // Сохраняем дефолтные категории в Firestore
                FirestoreHelper.saveDefaultCategoriesToFirestore(context)

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

            // Обновление или добавление категорий
            defaultCategories.forEach { newCategory ->
                val existingCategory = existingCategories.find { it.iconName == newCategory.iconName }
                if (existingCategory == null) {
                    dao.insert(newCategory)  // Если категории нет, добавляем
                } else if (existingCategory.name != newCategory.name) {
                    dao.update(existingCategory.copy(name = newCategory.name))  // Обновляем название
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
    @JvmStatic
    fun getDefaultCategories(res: Resources): List<Category> {
        return listOf(
            // Доходы
            Category(
                uuid = "income_salary_uuid", // фиксированный UUID
                name = res.getString(R.string.category_salary),
                iconName = "ic_salary",
                iconResId = R.drawable.ic_salary,
                isIncome = true,
                color = 0xFF4CAF50.toInt()
            ),
            Category(
                uuid = "income_investments_uuid",
                name = res.getString(R.string.category_investments),
                iconName = "ic_investments",
                iconResId = R.drawable.ic_investments,
                isIncome = true,
                color = 0xFF2196F3.toInt()
            ),
            Category(
                uuid = "income_gift_uuid",
                name = res.getString(R.string.category_gift),
                iconName = "ic_gift",
                iconResId = R.drawable.ic_gift,
                isIncome = true,
                color = 0xFFFFC107.toInt()
            ),

            // Расходы
            Category(
                uuid = "expense_food_uuid",
                name = res.getString(R.string.category_food),
                iconName = "ic_food",
                iconResId = R.drawable.ic_food,
                isIncome = false,
                color = 0xFFF44336.toInt()
            ),
            Category(
                uuid = "expense_transport_uuid",
                name = res.getString(R.string.category_transport),
                iconName = "ic_transport",
                iconResId = R.drawable.ic_transport,
                isIncome = false,
                color = 0xFF9C27B0.toInt()
            ),
            Category(
                uuid = "expense_medical_uuid",
                name = res.getString(R.string.category_medical),
                iconName = "ic_medical",
                iconResId = R.drawable.ic_medical,
                isIncome = false,
                color = 0xFF009688.toInt()
            ),
            Category(
                uuid = "expense_shopping_uuid",
                name = res.getString(R.string.category_shopping),
                iconName = "ic_shopping",
                iconResId = R.drawable.ic_shopping,
                isIncome = false,
                color = 0xFFFF9800.toInt()
            ),
            Category(
                uuid = "expense_home_uuid",
                name = res.getString(R.string.category_home),
                iconName = "ic_myhome",
                iconResId = R.drawable.ic_myhome,
                isIncome = false,
                color = 0xFF3F51B5.toInt()
            ),
            Category(
                uuid = "expense_entertainment_uuid",
                name = res.getString(R.string.category_entertainment),
                iconName = "ic_entertainment",
                iconResId = R.drawable.ic_entertainment,
                isIncome = false,
                color = 0xFFE91E63.toInt()
            )
        )
    }
}