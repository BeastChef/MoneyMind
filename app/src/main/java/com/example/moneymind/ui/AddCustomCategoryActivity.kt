package com.example.moneymind.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.viewmodel.CategoryViewModel
import com.example.moneymind.viewmodel.CategoryViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddCustomCategoryActivity : AppCompatActivity() {

    private lateinit var inputCategoryName: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var selectedIconView: ImageView
    private lateinit var layout: LinearLayout

    private var selectedIconResId: Int = R.drawable.ic_category_default

    private val categoryViewModel: CategoryViewModel by lazy {
        val dao = AppDatabase.getDatabase(applicationContext).categoryDao()
        val repository = CategoryRepository(dao)
        ViewModelProvider(this, CategoryViewModelFactory(repository))[CategoryViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_custom_category)

        inputCategoryName = findViewById(R.id.inputCategoryName)
        saveButton = findViewById(R.id.saveCategoryButton)
        layout = findViewById(R.id.addCategoryLayout)

        // Добавляем иконку в layout
        selectedIconView = ImageView(this).apply {
            setImageResource(selectedIconResId)
            layoutParams = LinearLayout.LayoutParams(200, 200)
            setOnClickListener {
                showIconGridDialog()
            }
        }
        layout.addView(selectedIconView, 1)

        // Кнопка "Сохранить"
        saveButton.setOnClickListener {
            val name = inputCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveCustomCategory(name)
            } else {
                Toast.makeText(this, "Введите название категории", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showIconGridDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_icon_grid, null)
        val gridView = dialogView.findViewById<GridView>(R.id.iconGridView)

        val iconResArray = resources.obtainTypedArray(R.array.icon_res_ids)
        val icons = (0 until iconResArray.length()).map { iconResArray.getResourceId(it, 0) }
        iconResArray.recycle()

        var tempSelected = selectedIconResId

        val adapter = object : BaseAdapter() {
            override fun getCount() = icons.size
            override fun getItem(position: Int) = icons[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return ImageView(this@AddCustomCategoryActivity).apply {
                    setImageResource(icons[position])
                    layoutParams = AbsListView.LayoutParams(150, 150)
                    setPadding(12, 12, 12, 12)
                }
            }
        }

        gridView.adapter = adapter
        gridView.setOnItemClickListener { _, _, position, _ ->
            tempSelected = icons[position]
        }

        AlertDialog.Builder(this)
            .setTitle("Выберите иконку")
            .setView(dialogView)
            .setPositiveButton("Выбрать") { _, _ ->
                selectedIconResId = tempSelected
                selectedIconView.setImageResource(selectedIconResId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveCustomCategory(name: String) {
        val isIncome = intent.getStringExtra("CATEGORY_TYPE") == "income"
        val iconName = resources.getResourceEntryName(selectedIconResId)

        val category = Category(
            name = name,
            iconResId = selectedIconResId,
            iconName = iconName,
            isIncome = isIncome
        )

        lifecycleScope.launch {
            categoryViewModel.insert(category)
            Toast.makeText(this@AddCustomCategoryActivity, "Категория добавлена", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }
}