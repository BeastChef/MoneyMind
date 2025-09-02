package com.example.moneymind.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import java.util.UUID

@Entity(
    tableName = "categories",
    indices = [Index(value = ["uuid"], unique = true)] // 🔑 уникальный индекс для защиты от дублей
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // 🔑 глобальный UUID, одинаковый в Room и Firestore
    val uuid: String = UUID.randomUUID().toString(),

    val name: String = "",
    val iconName: String = "",
    val iconResId: Int = 0,

    @get:PropertyName("isIncome")
    @set:PropertyName("isIncome")
    @ColumnInfo(name = "is_income")
    var isIncome: Boolean = false,

    // 🎨 цвет категории
    val color: Int = 0
) {
    // Firestore требует пустой конструктор
    constructor() : this(0, UUID.randomUUID().toString(), "", "", 0, false, 0)

    override fun toString(): String {
        return "Category(id=$id, uuid=$uuid, name='$name', iconName='$iconName', iconResId=$iconResId, isIncome=$isIncome, color=$color)"
    }
}