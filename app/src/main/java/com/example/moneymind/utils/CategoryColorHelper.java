package com.example.moneymind.utils;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class CategoryColorHelper {

    private static final Map<String, Integer> categoryColors = new HashMap<>();

    static {
        categoryColors.put("Еда", Color.parseColor("#FFA726"));           // оранжевый
        categoryColors.put("Одежда", Color.parseColor("#42A5F5"));        // синий
        categoryColors.put("Транспорт", Color.parseColor("#66BB6A"));     // зелёный
        categoryColors.put("Развлечения", Color.parseColor("#AB47BC"));   // фиолетовый
        categoryColors.put("Дом", Color.parseColor("#8D6E63"));           // коричневый
        categoryColors.put("Здоровье", Color.parseColor("#EF5350"));      // красный
        categoryColors.put("Образование", Color.parseColor("#29B6F6"));   // голубой
        categoryColors.put("Подарки", Color.parseColor("#FFCA28"));       // жёлтый
        categoryColors.put("Другое", Color.GRAY);                         // серый
    }

    public static int getColorForCategory(String category) {
        if (categoryColors.containsKey(category)) {
            return categoryColors.get(category);
        } else {
            return Color.DKGRAY; // дефолт
        }
    }
}