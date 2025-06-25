package com.example.moneymind.utils

import com.example.moneymind.data.Category
import com.example.moneymind.model.CustomCategoryEntity
import com.example.moneymind.model.CategoryItem

fun Category.toCategoryItem(): CategoryItem {
    return CategoryItem(
        id = this.id,
        name = this.name,
        iconResId = this.iconResId,
        iconName = this.iconName,
        isIncome = this.isIncome
    )
}

fun CustomCategoryEntity.toCategoryItem(): CategoryItem {
    return CategoryItem(
        id = this.id,
        name = this.name,
        iconResId = this.iconResId,
        iconName = this.iconName,
        isIncome = this.isIncome
    )
}