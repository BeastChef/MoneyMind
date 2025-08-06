package com.example.moneymind.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",              // Добавлена пустая строка по умолчанию
    val amount: Double = 0.0,            // Добавлен дефолтный нулевой баланс
    val category: String = "",           // Добавлена пустая строка по умолчанию
    val date: Long = 0L,                 // Добавлена дефолтная дата (например, 0)
    val type: String = "",              // Добавлена пустая строка по умолчанию
    val iconName: String = "",          // Добавлена пустая строка по умолчанию
    @ColumnInfo(name = "category_color") val categoryColor: Int = 0, // Добавлен дефолтный цвет
    val note: String? = null            // Если значение пустое, оставляем null
) {
    // Конструктор по умолчанию нужен для десериализации Firebase
    constructor() : this(0, "", 0.0, "", 0L, "", "", 0, null)

    // Переопределенный метод toString()
    override fun toString(): String {
        return "Expense(id=$id, title='$title', amount=$amount, category='$category', date=$date, type='$type', iconName='$iconName', categoryColor=$categoryColor, note=$note)"
    }
}