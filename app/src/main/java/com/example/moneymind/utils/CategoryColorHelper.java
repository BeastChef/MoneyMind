package com.example.moneymind.utils;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryColorHelper {

    private static final List<String> pastelExpenseColors = new ArrayList<>();
    private static final List<String> pastelIncomeColors = new ArrayList<>();

    static {
        // Расходы (без зелёного и синего)
        pastelExpenseColors.addAll(Arrays.asList(
                "#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#FFECB3",
                "#FFE0B2", "#FFCCBC", "#D7CCC8", "#F0F4C3", "#FFF176",
                "#FFD54F", "#FFB74D", "#FFAB91", "#FF8A65", "#FF8A80",
                "#FBE9E7", "#F9FBE7", "#FFF59D", "#F1F8E9", "#FFF9C4"
        ));

        // Доходы (без красного и оранжевого)
        pastelIncomeColors.addAll(Arrays.asList(
                "#BBDEFB", "#C5CAE9", "#B2DFDB", "#C8E6C9", "#DCEDC8",
                "#E0F2F1", "#E1F5FE", "#B2EBF2", "#D1C4E9", "#CE93D8",
                "#B39DDB", "#90CAF9", "#80DEEA", "#AED581", "#81C784",
                "#4DB6AC", "#4DD0E1", "#64B5F6", "#7986CB", "#CFD8DC"
        ));

        // Удваиваем для надёжного хэш-распределения
        pastelExpenseColors.addAll(pastelExpenseColors);
        pastelIncomeColors.addAll(pastelIncomeColors);
    }

    /**
     * Получает уникальный цвет для категории по ключу (iconName или name)
     * @param key Ключ категории
     * @param isIncome true — доход, false — расход
     */
    public static int getColorForCategoryKey(String key, boolean isIncome) {
        if (key == null) return Color.LTGRAY;
        int index = Math.abs(key.hashCode());

        List<String> palette = isIncome ? pastelIncomeColors : pastelExpenseColors;
        int colorIndex = index % palette.size();

        return Color.parseColor(palette.get(colorIndex));
    }
}