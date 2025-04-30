package com.example.moneymind.ui.choose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moneymind.databinding.ActivityChooseCategoryBinding
import com.example.moneymind.ui.category.CategoryItem

class ChooseCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isIncome = intent.getBooleanExtra("is_income", false)

        val categories = if (isIncome) {
            listOf(
                CategoryItem("Зарплата", R.drawable.ic_salary),
                CategoryItem("Дивиденды", R.drawable.ic_investments),
                CategoryItem("Подарки", R.drawable.ic_gift)
            )
        } else {
            listOf(
                CategoryItem("Еда", R.drawable.ic_food),
                CategoryItem("Транспорт", R.drawable.ic_transport),
                CategoryItem("Медицина", R.drawable.ic_medical),
                CategoryItem("Развлечения", R.drawable.ic_entertainment),
                CategoryItem("Жильё", R.drawable.ic_home),
                CategoryItem("Покупки", R.drawable.ic_shopping),
                CategoryItem("Другое", R.drawable.ic_other)
            )
        }

        val adapter = ChooseCategoryAdapter(categories) { selectedCategory ->
            val resultIntent = Intent()
            resultIntent.putExtra("selected_category", selectedCategory.name)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.recyclerViewCategories.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewCategories.adapter = adapter
    }
}