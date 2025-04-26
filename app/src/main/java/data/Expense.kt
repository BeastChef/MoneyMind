package com.example.moneymind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions") // ✅ новое имя таблицы для универсальности
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,               // Название товара или услуги
    val amount: Double,              // Сумма
    val category: String,            // Категория (автокатегория)
    val note: String? = null,        // Примечание
    val date: Long,                  // Дата
    val type: String = "expense"     // ✅ Тип записи: "expense" или "income"
)