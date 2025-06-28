package com.example.moneymind.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val type: String, // "income" или "expense"
    val iconName: String,
    @ColumnInfo(name = "category_color") val categoryColor: Int,
    val note: String? = null
)