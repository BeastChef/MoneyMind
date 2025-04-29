package com.example.moneymind.ui.choose

import com.example.moneymind.R

object CategoryIcons {

    val expenseCategories = listOf(
        CategoryIcon(R.string.category_food, R.drawable.ic_food),
        CategoryIcon(R.string.category_transport, R.drawable.ic_transport),
        CategoryIcon(R.string.category_medical, R.drawable.ic_medical),
        CategoryIcon(R.string.category_home, R.drawable.ic_home),
        CategoryIcon(R.string.category_entertainment, R.drawable.ic_entertainment),
        CategoryIcon(R.string.category_shopping, R.drawable.ic_shopping),
        CategoryIcon(R.string.category_other, R.drawable.ic_other)
    )

    val incomeCategories = listOf(
        CategoryIcon(R.string.category_salary, R.drawable.ic_salary, isIncome = true),
        CategoryIcon(R.string.category_investments, R.drawable.ic_investments, isIncome = true),
        CategoryIcon(R.string.category_gift, R.drawable.ic_gift, isIncome = true),
        CategoryIcon(R.string.category_other_income, R.drawable.ic_other_income, isIncome = true)
    )
}