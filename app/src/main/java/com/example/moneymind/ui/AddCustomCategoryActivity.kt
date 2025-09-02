package com.example.moneymind.ui

import android.os.Bundle
import android.widget.*

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.model.CustomCategoryEntity
import com.example.moneymind.ui.adapter.IconAdapter
import com.example.moneymind.utils.FirestoreHelper
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
                Toast.makeText(this, getString(R.string.enter_category_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val iconName = resources.getResourceEntryName(selectedIconResId)
            val color = (0xFF000000..0xFFFFFFFF).random().toInt()

            lifecycleScope.launch {
                // 🔥 Проверяем по имени и типу (чтобы "Лес" доход ≠ "Лес" расход)
                val existingCategory = viewModel.getCategoryByNameAndType(name, isIncome)

                if (existingCategory != null) {
                    // ⚡ Используем старый UUID
                    val updatedCategory = existingCategory.copy(
                        name = name,
                        iconResId = selectedIconResId,
                        iconName = iconName,
                        isIncome = isIncome,
                        color = color
                    )

                    viewModel.updateCustom(updatedCategory)
                    FirestoreHelper.saveCustomCategoryToFirestore(updatedCategory)

                    Toast.makeText(this@AddCustomCategoryActivity, getString(R.string.updated), Toast.LENGTH_SHORT).show()
                } else {
                    // Создаём новую с новым UUID
                    val newCategory = CustomCategoryEntity(
                        id = 0,
                        uuid = java.util.UUID.randomUUID().toString(),
                        name = name,
                        iconResId = selectedIconResId,
                        iconName = iconName,
                        isIncome = isIncome,
                        color = color
                    )

                    viewModel.insertCustom(newCategory)
                    FirestoreHelper.saveCustomCategoryToFirestore(newCategory)

                    Toast.makeText(this@AddCustomCategoryActivity, getString(R.string.category_added), Toast.LENGTH_SHORT).show()
                }

                setResult(RESULT_OK)
                finish()
            }
        }


    }
}