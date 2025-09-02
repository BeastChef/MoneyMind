package com.example.moneymind.model

data class CategoryItem(
    val id: Int,
    val uuid: String,          // 🚀 уникальный идентификатор
    val name: String,
    val iconResId: Int,
    val iconName: String,
    val isIncome: Boolean,
    val isCustom: Boolean,
    val color: Int,            // 🚀 чтобы не терять цвет
    val amount: Double? = null // сумма (например, для статистики)
)