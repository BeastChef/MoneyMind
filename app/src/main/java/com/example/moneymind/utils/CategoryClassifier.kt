package com.example.moneymind.utils

object CategoryClassifier {
    private val categoryMap = mapOf(
        "еда" to listOf("хлеб", "молоко", "сыр", "фрукты", "овощи", "йогурт"),
        "транспорт" to listOf("бензин", "метро", "проезд", "автобус", "троллейбус"),
        "развлечения" to listOf("кино", "игра", "подписка", "музыка"),
        "одежда" to listOf("футболка", "куртка", "джинсы", "ботинки"),
        "здоровье" to listOf("аптека", "лекарства", "анальгин", "витамины"),
        "другое" to listOf()
    )

    fun classify(name: String): String {
        val lower = name.lowercase()
        for ((category, keywords) in categoryMap) {
            if (keywords.any { lower.contains(it) }) {
                return category.replaceFirstChar { it.uppercaseChar() }
            }
        }
        return "Другое"
    }
}