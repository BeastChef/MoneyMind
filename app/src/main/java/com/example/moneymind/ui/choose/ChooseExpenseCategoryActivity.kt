package com.example.moneymind.ui.choose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.ui.adapters.CategoryIconAdapter
import com.example.moneymind.ui.AddExpenseActivity

class ChooseExpenseCategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryIconAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_category)

        recyclerView = findViewById(R.id.categoryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = CategoryIconAdapter(getExpenseCategories()) { category ->
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("selected_category", category)
            intent.putExtra("is_income", false)
            startActivity(intent)
            finish()
        }

        recyclerView.adapter = adapter
    }

    private fun getExpenseCategories(): List<String> {
        return listOf(
            "Еда", "Транспорт", "Жильё", "Медицина", "Развлечения", "Путешествия", "Покупки", "Спорт", "Другое"
        )
    }
}