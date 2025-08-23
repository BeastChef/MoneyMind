package com.example.moneymind.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

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

class ChooseExpenseCategoryActivity : BaseActivityK() {

    private lateinit var adapter: CategoryAdapter
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_expense_category)

        findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val tabLayout = findViewById<TabLayout>(R.id.categoryTabLayout)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.income)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.expense)))
        tabLayout.getTabAt(1)?.select()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    val intent = Intent(this@ChooseExpenseCategoryActivity, ChooseIncomeCategoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Инициализация ViewModel
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = CategoryRepository(db.categoryDao(), db.customCategoryDao())
        val factory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_expense_categories)
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
            intent.putExtra("CATEGORY_TYPE", "expense")
            startActivityForResult(intent, 100)
        }
    }

    private fun observeCategories() {
        categoryViewModel.getCategories(isIncome = false).observe(this) { defaultList ->
            categoryViewModel.getCustomCategories(isIncome = false).observe(this) { customList ->
                val combined = (defaultList.map { it.toCategoryItem() } + customList.map { it.toCategoryItem() })
                    .distinctBy { it.name }  // Убираем дубликаты по имени категории
                adapter.submitList(combined)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            observeCategories()
        }
    }
}