package com.example.moneymind.model

data class CustomCategory(
    val id: Int = 0,
    val name: String,
    val iconResId: Int,
    val isIncome: Boolean
)