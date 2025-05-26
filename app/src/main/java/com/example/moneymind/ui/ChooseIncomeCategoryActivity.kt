package com.example.moneymind.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.ui.adapter.CategoryAdapter
import com.example.moneymind.viewmodel.CategoryViewModel
import com.example.moneymind.viewmodel.CategoryViewModelFactory

class ChooseIncomeCategoryActivity : AppCompatActivity() {

    private lateinit var adapter: CategoryAdapter

    private val categoryViewModel: CategoryViewModel by lazy {
        val dao = AppDatabase.getDatabase(applicationContext).categoryDao()
        val repository = CategoryRepository(dao)
        val factory = CategoryViewModelFactory(repository)
        ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_income_category)

        val addCustomCategoryButton = findViewById<Button>(R.id.btn_add_custom_category)
        addCustomCategoryButton.setOnClickListener {
            val intent = Intent(this, AddCustomCategoryActivity::class.java)
            intent.putExtra("CATEGORY_TYPE", "income")
            startActivityForResult(intent, 101)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        adapter = CategoryAdapter { category ->
            val resultIntent = Intent().apply {
                putExtra("selected_category_id", category.id)
                putExtra("is_income", true) // 👈 передаём в AddExpenseActivity
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        observeCategories()
    }

    private fun observeCategories() {
        categoryViewModel.getCategories(isIncome = true).observe(this) { categories ->
            adapter.submitList(categories)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            observeCategories()
        }
    }
}