package com.example.moneymind.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.data.AppDatabase
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.model.CustomCategoryEntity
import com.example.moneymind.ui.adapter.IconAdapter
import com.example.moneymind.viewmodel.CategoryViewModel
import com.example.moneymind.viewmodel.CategoryViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var inputName: TextInputEditText
    private lateinit var iconView: ImageView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var btnCancel: ImageButton
    private lateinit var layout: LinearLayout

    private var selectedIconResId = R.drawable.ic_category_default
    private var iconName = "ic_category_default"
    private var categoryId: Int = -1
    private var isIncome: Boolean = false
    private var isCustom: Boolean = true

    private lateinit var viewModel: CategoryViewModel
    private var categoryToEdit: CustomCategoryEntity? = null
    private var categoryDefault: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        inputName = findViewById(R.id.editCategoryName)
        iconView = findViewById(R.id.editCategoryIcon)
        btnSave = findViewById(R.id.btnSaveCategory)
        btnDelete = findViewById(R.id.btnDeleteCategory)
        btnCancel = findViewById(R.id.btnCancelCategory)
        layout = findViewById(R.id.editCategoryLayout)

        categoryId = intent.getIntExtra("category_id", -1)
        isIncome = intent.getBooleanExtra("category_is_income", false)
        isCustom = intent.getBooleanExtra("is_custom", true)

        val dao = AppDatabase.getDatabase(applicationContext)
        val repository = CategoryRepository(dao.categoryDao(), dao.customCategoryDao())
        viewModel = ViewModelProvider(this, CategoryViewModelFactory(repository))[CategoryViewModel::class.java]

        lifecycleScope.launch {
            if (isCustom) {
                val list = withContext(Dispatchers.IO) {
                    dao.customCategoryDao().getAllNow(isIncome)
                }
                categoryToEdit = list.find { it.id == categoryId }
                categoryToEdit?.let { category ->
                    inputName.setText(category.name)
                    selectedIconResId = category.iconResId
                    iconName = category.iconName
                    iconView.setImageResource(selectedIconResId)
                }
            } else {
                val cat = withContext(Dispatchers.IO) {
                    dao.categoryDao().getById(categoryId)
                }
                categoryDefault = cat
                categoryDefault?.let { category ->
                    inputName.setText(category.name)
                    selectedIconResId = category.iconResId
                    iconName = category.iconName
                    iconView.setImageResource(selectedIconResId)
                }
            }

            if (categoryToEdit == null && categoryDefault == null) {
                Toast.makeText(this@EditCategoryActivity, "Категория не найдена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        iconView.setOnClickListener { showIconPickerBottomSheet() }

        btnSave.setOnClickListener {
            val name = inputName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (isCustom && categoryToEdit != null) {
                    val updated = categoryToEdit!!.copy(name = name, iconResId = selectedIconResId, iconName = iconName)
                    viewModel.updateCustom(updated)
                } else if (categoryDefault != null) {
                    val updated = categoryDefault!!.copy(name = name, iconResId = selectedIconResId, iconName = iconName)
                    viewModel.update(updated)
                }

                Toast.makeText(this@EditCategoryActivity, "Категория обновлена", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK, Intent().putExtra("edited_type", if (isIncome) "income" else "expense"))
                finish()
            }
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Удалить категорию?")
                .setMessage("Это действие нельзя отменить.")
                .setPositiveButton("Удалить") { _, _ ->
                    lifecycleScope.launch {
                        if (isCustom && categoryToEdit != null) {
                            viewModel.deleteCustomById(categoryToEdit!!.id)
                        } else if (categoryDefault != null) {
                            viewModel.deleteCategoryById(categoryDefault!!.id)
                        }

                        Toast.makeText(this@EditCategoryActivity, "Категория удалена", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK, Intent().putExtra("deleted_type", if (isIncome) "income" else "expense"))
                        finish()
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showIconPickerBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_icon_picker, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.iconRecyclerView)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmIconPicker)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelIconPicker)

        val iconResArray = resources.obtainTypedArray(R.array.icon_res_ids)
        val icons = (0 until iconResArray.length()).map { iconResArray.getResourceId(it, 0) }
        iconResArray.recycle()

        var tempSelectedResId = selectedIconResId

        val adapter = IconAdapter(icons) { selected ->
            tempSelectedResId = selected
        }

        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter

        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm?.setOnClickListener {
            selectedIconResId = tempSelectedResId
            iconView.setImageResource(selectedIconResId)
            iconName = resources.getResourceEntryName(selectedIconResId)
            dialog.dismiss()
        }

        dialog.show()
    }
}