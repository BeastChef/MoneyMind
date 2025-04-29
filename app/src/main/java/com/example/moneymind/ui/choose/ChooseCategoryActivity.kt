package com.example.moneymind.ui.choose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moneymind.databinding.ActivityChooseCategoryBinding
import com.example.moneymind.model.CategoryItem

class ChooseCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isIncome = intent.getBooleanExtra("is_income", false)

        val categories = if (isIncome) {
            listOf(
                CategoryItem("Зарплата", "ic_salary"),
                CategoryItem("Дивиденды", "ic_dividends"),
                CategoryItem("Подарки", "ic_gift")
            )
        } else {
            listOf(
                CategoryItem("Еда", "ic_food"),
                CategoryItem("Транспорт", "ic_transport"),
                CategoryItem("Медицина", "ic_medical"),
                CategoryItem("Развлечения", "ic_entertainment"),
                CategoryItem("Жильё", "ic_home")
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