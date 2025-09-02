package com.example.moneymind.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,         // Название категории
    val iconResId: Int,       // ID ресурса иконки (например, R.drawable.ic_food)
    val iconName: String,     // Имя ресурса (например, "ic_food")
    val uuid: String,
    val color: Int,
    val isIncome: Boolean     // Тип: true = доход, false = расход

)