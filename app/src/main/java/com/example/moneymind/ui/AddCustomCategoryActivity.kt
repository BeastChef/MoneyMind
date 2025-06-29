package com.example.moneymind.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.model.CustomCategoryEntity
import com.example.moneymind.ui.adapter.IconAdapter
import com.example.moneymind.viewmodel.CategoryViewModel
import com.example.moneymind.viewmodel.CategoryViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddCustomCategoryActivity : BaseActivityK() {

    private lateinit var inputCategoryName: TextInputEditText
    private lateinit var iconRecycler: RecyclerView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var viewModel: CategoryViewModel
    private var selectedIconResId = R.drawable.ic_category_default
    private var isIncome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        // Инициализация
        inputCategoryName = findViewById(R.id.inputCategoryName)
        iconRecycler = findViewById(R.id.iconGridRecycler)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Получаем тип
        isIncome = intent.getStringExtra("CATEGORY_TYPE") == "income"

        // ViewModel
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = CategoryRepository(db.categoryDao(), db.customCategoryDao())
        val factory = CategoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        // Загружаем иконки
        val iconArray = resources.obtainTypedArray(R.array.icon_res_ids)
        val iconList = (0 until iconArray.length()).map { iconArray.getResourceId(it, 0) }
        iconArray.recycle()

        val adapter = IconAdapter(iconList) { resId ->
            selectedIconResId = resId
        }

        iconRecycler.layoutManager = GridLayoutManager(this, 4)
        iconRecycler.adapter = adapter

        btnCancel.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val name = inputCategoryName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val iconName = resources.getResourceEntryName(selectedIconResId)
            val category = CustomCategoryEntity(
                id = 0,
                name = name,
                iconResId = selectedIconResId,
                iconName = iconName,
                isIncome = isIncome
            )

            lifecycleScope.launch {
                viewModel.insertCustom(category)
                Toast.makeText(this@AddCustomCategoryActivity, "Категория добавлена", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}