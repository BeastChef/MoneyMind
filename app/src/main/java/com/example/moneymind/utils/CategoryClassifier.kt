package com.example.moneymind.utils

import android.content.Context
import com.example.moneymind.R
import java.util.*

object CategoryClassifier {

    fun classify(context: Context, input: String): String {
        val text = input.lowercase(Locale.getDefault())

        val locale = Locale.getDefault().language
        val isRussian = locale == "ru"
        val isEnglish = locale == "en"

        val foodKeywords = context.resources.getStringArray(
            if (isRussian) R.array.keywords_food_ru else R.array.keywords_food_en
        )
        val transportKeywords = context.resources.getStringArray(
            if (isRussian) R.array.keywords_transport_ru else R.array.keywords_transport_en
        )

        return when {
            containsKeyword(text, foodKeywords) -> if (isRussian) "Еда" else "Food"
            containsKeyword(text, transportKeywords) -> if (isRussian) "Транспорт" else "Transport"
            else -> if (isRussian) "Другое" else "Other"
        }
    }

    private fun containsKeyword(text: String, keywords: Array<String>): Boolean {
        return keywords.any { keyword -> text.contains(keyword, ignoreCase = true) }
    }
}