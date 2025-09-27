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

                // Удаляем все дефолтные категории из базы данных
                dao.deleteAll() // Очистим все категории из базы данных

                // Сохраняем текущий язык
                prefs.edit().putString(KEY_LAST_LANG, currentLang).apply()
            }
        }
    }

    // Этот метод теперь не нужен, так как мы не будем добавлять дефолтные категории
    @JvmStatic
    fun updateCategoriesIfNeeded(context: Context) {
        // Пропускаем добавление дефолтных категорий
    }
}