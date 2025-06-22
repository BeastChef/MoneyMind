package com.example.moneymind.model

data class CategoryItem(
    val name: String,
    val iconResId: Int,
    val amount: Double? = null // добавлено новое поле для суммы
)