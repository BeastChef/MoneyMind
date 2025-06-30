package com.example.moneymind.utils

import android.content.Context
import com.example.moneymind.R
import com.example.moneymind.data.Category

object DefaultCategoriesProvider {

    fun getDefaultExpenseCategories(context: Context): List<Category> = listOf(
        Category(
            name = context.getString(R.string.category_food),
            iconName = "ic_food",
            iconResId = R.drawable.ic_food,
            isIncome = false
        ),
        Category(
            name = context.getString(R.string.category_transport),
            iconName = "ic_transport",
            iconResId = R.drawable.ic_transport,
            isIncome = false
        ),
        Category(
            name = context.getString(R.string.category_medical),
            iconName = "ic_medical",
            iconResId = R.drawable.ic_medical,
            isIncome = false
        ),
        Category(
            name = context.getString(R.string.category_shopping),
            iconName = "ic_shopping",
            iconResId = R.drawable.ic_shopping,
            isIncome = false
        ),
        Category(
            name = context.getString(R.string.category_home),
            iconName = "ic_myhome",
            iconResId = R.drawable.ic_myhome,
            isIncome = false
        ),
        Category(
            name = context.getString(R.string.category_gift),
            iconName = "ic_gift",
            iconResId = R.drawable.ic_gift,
            isIncome = false
        ),
        Category(
            name = context.getString(R.string.category_entertainment),
            iconName = "ic_entertainment",
            iconResId = R.drawable.ic_entertainment,
            isIncome = false
        )
    )

    fun getDefaultIncomeCategories(context: Context): List<Category> = listOf(
        Category(
            name = context.getString(R.string.category_salary),
            iconName = "ic_salary",
            iconResId = R.drawable.ic_salary,
            isIncome = true
        ),
        Category(
            name = context.getString(R.string.category_investments),
            iconName = "ic_investments",
            iconResId = R.drawable.ic_investments,
            isIncome = true
        ),
        Category(
            name = context.getString(R.string.category_gift),
            iconName = "ic_gift",
            iconResId = R.drawable.ic_gift,
            isIncome = true
        )
    )
}