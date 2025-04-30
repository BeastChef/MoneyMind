package com.example.moneymind.ui.choose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.ui.AddExpenseActivity
import com.example.moneymind.ui.category.CategoryIconAdapter
import com.example.moneymind.ui.category.CategoryItem

class ChooseExpenseCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_expense_category)

        val recyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val adapter = CategoryIconAdapter(getExpenseCategories()) { category ->
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("selected_category", category.name)
            intent.putExtra("is_income", false)
            startActivity(intent)
            finish()
        }

        recyclerView.adapter = adapter
    }

    private fun getExpenseCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem("Еда", R.drawable.ic_food),
            CategoryItem("Транспорт", R.drawable.ic_transport),
            CategoryItem("Жильё", R.drawable.ic_myhome),
            CategoryItem("Медицина", R.drawable.ic_medical),
            CategoryItem("Развлечения", R.drawable.ic_entertainment),
            CategoryItem("Путешествия", R.drawable.ic_travel),
            CategoryItem("Покупки", R.drawable.ic_shopping),
            CategoryItem("Спорт", R.drawable.ic_sport),
            CategoryItem("Другое", R.drawable.ic_other)
        )
    }
}