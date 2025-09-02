package com.example.moneymind.utils

import com.example.moneymind.data.Category
import com.example.moneymind.model.CustomCategoryEntity
import com.example.moneymind.model.CategoryItem

// Для дефолтных категорий
fun Category.toCategoryItem(): CategoryItem {
    return CategoryItem(
        id = this.id,
        uuid = this.uuid,        // ✅ добавляем uuid
        name = this.name,
        iconResId = this.iconResId,
        iconName = this.iconName,
        isIncome = this.isIncome,
        isCustom = false,        // дефолтная
        color = this.color       // ✅ добавляем цвет
    )
}

// Для кастомных категорий
fun CustomCategoryEntity.toCategoryItem(): CategoryItem {
    return CategoryItem(
        id = this.id,
        uuid = this.uuid,        // ✅ добавляем uuid
        name = this.name,
        iconResId = this.iconResId,
        iconName = this.iconName,
        isIncome = this.isIncome,
        isCustom = true,         // кастомная
        color = this.color       // ✅ добавляем цвет
    )
}