package com.example.moneymind.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.model.CategoryItem
import com.example.moneymind.ui.adapter.CategoryAdapter
import com.example.moneymind.utils.toCategoryItem
import com.example.moneymind.viewmodel.CategoryViewModel
import com.example.moneymind.viewmodel.CategoryViewModelFactory
import com.google.android.material.tabs.TabLayout

class ChooseIncomeCategoryActivity : AppCompatActivity() {

    private lateinit var adapter: CategoryAdapter
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_income_category)

        findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val tabLayout = findViewById<TabLayout>(R.id.categoryTabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Доход"))
        tabLayout.addTab(tabLayout.newTab().setText("Расход"))
        tabLayout.getTabAt(0)?.select()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    val intent = Intent(this@ChooseIncomeCategoryActivity, ChooseExpenseCategoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = CategoryRepository(db.categoryDao(), db.customCategoryDao())
        val factory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        adapter = CategoryAdapter { category: CategoryItem ->
            val intent = Intent(this, AddExpenseActivity::class.java).apply {
                putExtra("selected_category", category.name)
                putExtra("selected_icon", category.iconName)
                putExtra("category_id", category.id)
                putExtra("is_income", category.isIncome)
                putExtra("is_custom", category.isCustom)
                intent.putExtra("is_custom", false)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter

        observeCategories()

        findViewById<Button>(R.id.btn_add_custom_category).setOnClickListener {
            val intent = Intent(this, AddCustomCategoryActivity::class.java)
            intent.putExtra("CATEGORY_TYPE", "income")
            startActivityForResult(intent, 101)
        }
    }

    private fun observeCategories() {
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = CategoryRepository(db.categoryDao(), db.customCategoryDao())

        // Получаем и дефолтные, и кастомные категории
        categoryViewModel.getCategories(isIncome = true).observe(this) { defaultList ->
            categoryViewModel.getCustomCategories(isIncome = true).observe(this) { customList ->
                val combined = (defaultList.map { it.toCategoryItem() } + customList.map { it.toCategoryItem() })
                adapter.submitList(combined)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            observeCategories()
        }
    }
}