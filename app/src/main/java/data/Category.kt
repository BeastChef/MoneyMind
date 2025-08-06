package com.example.moneymind.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",              // Добавлена пустая строка по умолчанию
    val iconName: String = "",          // Добавлена пустая строка по умолчанию
    val iconResId: Int = 0,             // Добавлен дефолтный ID иконки
    @ColumnInfo(name = "is_income") val isIncome: Boolean = false  // Добавлен дефолтный флаг (расход/доход)
) {
    // Конструктор по умолчанию нужен для десериализации Firebase
    constructor() : this(0, "", "", 0, false)  // Пустой конструктор для Firestore

    // Переопределенный метод toString()
    override fun toString(): String {
        return "Category(id=$id, name='$name', iconName='$iconName', iconResId=$iconResId, isIncome=$isIncome)"
    }
}