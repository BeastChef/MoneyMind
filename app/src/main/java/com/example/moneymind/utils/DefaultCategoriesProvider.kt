package com.example.moneymind.utils

import com.example.moneymind.R
import com.example.moneymind.data.Category

object DefaultCategoriesProvider {

    fun getDefaultExpenseCategories(): List<Category> = listOf(
        Category(
            name = "@string/category_food",
            iconName = "ic_food",
            iconResId = R.drawable.ic_food,
            isIncome = false
        ),
        Category(
            name = "Транспорт",
            iconName = "ic_transport",
            iconResId = R.drawable.ic_transport,
            isIncome = false
        ),
        Category(
            name = "Медицина",
            iconName = "ic_medical",
            iconResId = R.drawable.ic_medical,
            isIncome = false
        ),
        Category(
            name = "Покупки",
            iconName = "ic_shopping",
            iconResId = R.drawable.ic_shopping,
            isIncome = false
        ),
        Category(
            name = "Дом",
            iconName = "ic_myhome",
            iconResId = R.drawable.ic_myhome,
            isIncome = false
        ),
        Category(
            name = "Подарки",
            iconName = "ic_gift",
            iconResId = R.drawable.ic_gift,
            isIncome = false
        ),
        Category(
            name = "Развлечения",
            iconName = "ic_entertainment",
            iconResId = R.drawable.ic_entertainment,
            isIncome = false
        )
    )

    fun getDefaultIncomeCategories(): List<Category> = listOf(
        Category(
            name = "Зарплата",
            iconName = "ic_salary",
            iconResId = R.drawable.ic_salary,
            isIncome = true
        ),
        Category(
            name = "Инвестиции",
            iconName = "ic_investments",
            iconResId = R.drawable.ic_investments,
            isIncome = true
        ),
        Category(
            name = "Подарки",
            iconName = "ic_gift",
            iconResId = R.drawable.ic_gift,
            isIncome = true
        )
    )
}