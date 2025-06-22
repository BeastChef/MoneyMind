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
import com.example.moneymind.ui.adapter.CategoryAdapter
import com.example.moneymind.viewmodel.CategoryViewModel
import com.example.moneymind.viewmodel.CategoryViewModelFactory
import com.google.android.material.tabs.TabLayout

class ChooseIncomeCategoryActivity : AppCompatActivity() {

    private lateinit var adapter: CategoryAdapter
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_income_category)

        // Кнопка "Отмена"
        findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // TabLayout: Доход / Расход
        val tabLayout = findViewById<TabLayout>(R.id.categoryTabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Доход"))
        tabLayout.addTab(tabLayout.newTab().setText("Расход"))
        tabLayout.getTabAt(0)?.select() // выбрать "Доход" по умолчанию

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    // Перейти в экран расходов
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

        // ViewModel
        val dao = AppDatabase.getDatabase(applicationContext).categoryDao()
        val repository = CategoryRepository(dao)
        val factory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        // RecyclerView с GridLayoutManager (3 столбца)
        val recyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        adapter = CategoryAdapter { category ->
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("selected_category", category.name)
            intent.putExtra("selected_icon", category.iconName)
            intent.putExtra("is_income", true)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter

        observeCategories()

        // Анимация "рассвет"
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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