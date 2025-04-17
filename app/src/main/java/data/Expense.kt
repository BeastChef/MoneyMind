package com.example.moneymind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,             // сумма
    val category: String,          // категория (автокатегория)
    val note: String? = null,      // примечание (введённое пользователем, например "молоко")
    val date: Long                 // дата в миллисекундах (удобно для сортировки)
)