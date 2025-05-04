package com.example.moneymind.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.model.CustomCategoryEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddCustomCategoryActivity : AppCompatActivity() {

    private lateinit var inputCategoryName: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var selectedIconView: ImageView

    private var selectedIconResId: Int = R.drawable.ic_category_default // Значение по умолчанию

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_custom_category)

        inputCategoryName = findViewById(R.id.inputCategoryName)
        saveButton = findViewById(R.id.saveCategoryButton)

        // Добавим отображение и выбор иконки
        selectedIconView = ImageView(this).apply {
            setImageResource(selectedIconResId)
            layoutParams = android.widget.LinearLayout.LayoutParams(200, 200)
            setOnClickListener {
                showIconPickerDialog()
            }
        }

        val layout = findViewById<android.widget.LinearLayout>(R.id.addCategoryLayout)
        layout.addView(selectedIconView, 1) // Вставляем иконку после поля ввода

        saveButton.setOnClickListener {
            val categoryName = inputCategoryName.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                saveCustomCategory(categoryName)
            } else {
                Toast.makeText(this, "Введите название категории", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showIconPickerDialog() {
        val iconNames = resources.getStringArray(R.array.icon_names)
        val iconResIds = resources.obtainTypedArray(R.array.icon_res_ids)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, iconNames)

        AlertDialog.Builder(this)
            .setTitle("Выберите иконку")
            .setAdapter(adapter) { _, which ->
                selectedIconResId = iconResIds.getResourceId(which, R.drawable.ic_category_default)
                selectedIconView.setImageResource(selectedIconResId)
            }
            .setNegativeButton("Отмена", null)
            .show()

        iconResIds.recycle()
    }

    private fun saveCustomCategory(name: String) {
        val category = CustomCategoryEntity(name = name, iconResId = selectedIconResId)

        val db = AppDatabase.getDatabase(this)
        db.customCategoryDao().insert(category)

        Toast.makeText(this, "Категория добавлена", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK, Intent()) // Можно обновить список в вызывающей активности
        finish()
    }
}