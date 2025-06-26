package com.example.moneymind.model

data class CategoryItem(
    val id: Int,
    val name: String,
    val iconResId: Int,
    val iconName: String,
    val isIncome: Boolean,
    val isCustom: Boolean,
    val amount: Double? = null // ✅ Добавили поле суммы
)