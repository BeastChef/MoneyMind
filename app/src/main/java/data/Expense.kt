package com.example.moneymind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,              // 🆕 Название товара или услуги
    val amount: Double,            // сумма
    val category: String,          // категория (автокатегория)
    val note: String? = null,      // примечание
    val date: Long                 // дата
)