package com.example.moneymind.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.model.CustomCategoryEntity
import kotlinx.coroutines.launch
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.BaseAdapter

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var iconView: ImageView
    private lateinit var nameInput: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var iconResId: Int = R.drawable.ic_category_default
    private lateinit var iconName: String
    private var categoryId: Int = -1
    private var isIncome: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        iconView = findViewById(R.id.editCategoryIcon)
        nameInput = findViewById(R.id.editCategoryName)
        saveButton = findViewById(R.id.saveCategoryButton)
        deleteButton = findViewById(R.id.deleteCategoryButton)

        // Получение данных из Intent
        categoryId = intent.getIntExtra("category_id", -1)
        val name = intent.getStringExtra("category_name") ?: ""
        iconName = intent.getStringExtra("category_icon") ?: "ic_money"
        isIncome = intent.getBooleanExtra("category_is_income", false)
        iconResId = resources.getIdentifier(iconName, "drawable", packageName)

        nameInput.setText(name)
        iconView.setImageResource(iconResId)

        iconView.setOnClickListener { showIconPicker() }

        saveButton.setOnClickListener { saveCategory() }
        deleteButton.setOnClickListener { confirmDelete() }
    }

    private fun showIconPicker() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_grid, null)
        val gridView = dialogView.findViewById<GridView>(R.id.iconGridView)

        val icons = resources.obtainTypedArray(R.array.icon_res_ids)
        val iconList = (0 until icons.length()).map { icons.getResourceId(it, 0) }
        icons.recycle()

        gridView.adapter = object : BaseAdapter() {
            override fun getCount() = iconList.size
            override fun getItem(position: Int) = iconList[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return ImageView(this@EditCategoryActivity).apply {
                    setImageResource(iconList[position])
                    layoutParams = AbsListView.LayoutParams(150, 150)
                    setPadding(12, 12, 12, 12)
                }
            }
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            iconResId = iconList[position]
            iconName = resources.getResourceEntryName(iconResId)
            iconView.setImageResource(iconResId)
        }

        AlertDialog.Builder(this)
            .setTitle("Выберите иконку")
            .setView(dialogView)
            .setPositiveButton("Выбрать", null)
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveCategory() {
        val newName = nameInput.text.toString().trim()
        if (newName.isEmpty()) {
            Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
            return
        }

        val updated = CustomCategoryEntity(
            id = categoryId,
            name = newName,
            iconResId = iconResId,
            iconName = iconName,
            isIncome = isIncome
        )

        lifecycleScope.launch {
            AppDatabase.getDatabase(applicationContext).customCategoryDao().update(updated)
            Toast.makeText(this@EditCategoryActivity, "Категория обновлена", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Удалить категорию?")
            .setMessage("Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    AppDatabase.getDatabase(applicationContext).customCategoryDao().deleteById(categoryId)
                    Toast.makeText(this@EditCategoryActivity, "Удалено", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}