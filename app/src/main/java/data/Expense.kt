package com.example.moneymind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val note: String? = null,
    val date: Long,
    val type: String = "expense",
    val iconName: String = "ic_money" // ✅ ← вот это обязательно
)
