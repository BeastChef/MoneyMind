package com.example.moneymind.ui.choose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.adapters.ChooseCategoryAdapter
import com.example.moneymind.model.CategoryItem

class ChooseCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_category)

        val isIncome = intent.getBooleanExtra("is_income", false)

        val categories = if (isIncome) {
            listOf(
                CategoryItem("Зарплата", R.drawable.ic_salary),
                CategoryItem("Дивиденды", R.drawable.ic_investments),
                CategoryItem("Подарки", R.drawable.ic_gift),
                CategoryItem("Другое", R.drawable.ic_other_income)
            )
        } else {
            listOf(
                CategoryItem("Еда", R.drawable.ic_food),
                CategoryItem("Транспорт", R.drawable.ic_transport),
                CategoryItem("Медицина", R.drawable.ic_medical),
                CategoryItem("Развлечения", R.drawable.ic_entertainment),
                CategoryItem("Жильё", R.drawable.ic_myhome),
                CategoryItem("Покупки", R.drawable.ic_shopping),
                CategoryItem("Другое", R.drawable.ic_other)
            )
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ChooseCategoryAdapter(categories) { selectedCategory ->
            val resultIntent = Intent()
            resultIntent.putExtra("selected_category", selectedCategory.name)
            resultIntent.putExtra("is_income", isIncome) // ✅ Передаём обратно
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}