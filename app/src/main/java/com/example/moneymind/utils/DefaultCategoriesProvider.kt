package com.example.moneymind.utils

import com.example.moneymind.R
import com.example.moneymind.data.Category

object DefaultCategoriesProvider {

    fun getDefaultExpenseCategories(): List<Category> = listOf(
        Category(name = "Еда", iconResId = R.drawable.ic_food, isIncome = false),
        Category(name = "Транспорт", iconResId = R.drawable.ic_transport, isIncome = false),
        Category(name = "Медицина", iconResId = R.drawable.ic_medical, isIncome = false),
        Category(name = "Покупки", iconResId = R.drawable.ic_shopping, isIncome = false),
        Category(name = "Дом", iconResId = R.drawable.ic_myhome, isIncome = false),
        Category(name = "Подарки", iconResId = R.drawable.ic_gift, isIncome = false),
        Category(name = "Развлечения", iconResId = R.drawable.ic_entertainment, isIncome = false)
    )

    fun getDefaultIncomeCategories(): List<Category> = listOf(
        Category(name = "Зарплата", iconResId = R.drawable.ic_salary, isIncome = true),
        Category(name = "Инвестиции", iconResId = R.drawable.ic_investments, isIncome = true),
        Category(name = "Подарки", iconResId = R.drawable.ic_gift, isIncome = true)
    )
}