package com.example.moneymind.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,              // Название категории
    val iconResId: Int,           // ID иконки

    @ColumnInfo(name = "is_income")  // 👈 важно!
    val isIncome: Boolean         // Доход или расход
)