package com.example.moneymind.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // 🔑 глобальный UUID расхода (для Firestore синка без дублей)
    val uuid: String = UUID.randomUUID().toString(),

    val title: String = "",              // старое поле – оставляем для совместимости
    val amount: Double = 0.0,
    val category: String = "",           // старое текстовое название категории
    val date: Long = System.currentTimeMillis(),
    val type: String = "",               // старое строковое поле ("расход"/"доход")
    val iconName: String = "",
    @ColumnInfo(name = "category_color") val categoryColor: Int = 0,
    val note: String? = null,

    // 🔑 новый ключ для связи категории по UUID (чтобы не зависеть от имени)
    val categoryUuid: String = "",

    // служебное поле для сортировки/синхронизации
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this(
        0,
        UUID.randomUUID().toString(),
        "",
        0.0,
        "",
        System.currentTimeMillis(),
        "",
        "",
        0,
        null,
        "",
        System.currentTimeMillis()
    )

    override fun toString(): String {
        return "Expense(id=$id, uuid=$uuid, title='$title', amount=$amount, category='$category', " +
                "date=$date, type='$type', iconName='$iconName', categoryColor=$categoryColor, " +
                "note=$note, categoryUuid='$categoryUuid', createdAt=$createdAt)"
    }
}