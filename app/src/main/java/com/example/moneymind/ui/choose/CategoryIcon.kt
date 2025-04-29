package com.example.moneymind.ui.choose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CategoryIcon(
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int,
    val isIncome: Boolean = false // 👈 флаг: это доход или расход
)

