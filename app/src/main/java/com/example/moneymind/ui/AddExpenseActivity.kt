package com.example.moneymind.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneymind.MoneyMindApp
import com.example.moneymind.R
import com.example.moneymind.data.Expense
import com.example.moneymind.utils.CategoryColorHelper
import com.example.moneymind.viewmodel.ExpenseViewModel
import com.example.moneymind.viewmodel.ExpenseViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var titleInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var categoryIcon: ImageView
    private lateinit var categoryName: TextView
    private lateinit var btnEditCategory: MaterialButton
    private lateinit var categoryLayout: View

    private var selectedCategory: String? = null
    private var selectedIconName: String = "ic_money"
    private var selectedIconResId: Int = R.drawable.ic_category_default
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var isIncome: Boolean = false
    private var selectedExpenseId: Int? = null

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory((application as MoneyMindApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Инициализация view
        titleInput = findViewById(R.id.inputTitle)
        amountInput = findViewById(R.id.inputAmount)
        dateInput = findViewById(R.id.inputDate)
        saveButton = findViewById(R.id.saveButton)
        categoryIcon = findViewById(R.id.selectedCategoryIcon)
        categoryName = findViewById(R.id.selectedCategoryText)
        btnEditCategory = findViewById(R.id.btnEditCategory)
        categoryLayout = findViewById(R.id.selectedCategoryLayout)

        // Получаем из intent
        selectedCategory = intent.getStringExtra("selected_category")
        selectedIconName = intent.getStringExtra("selected_icon") ?: "ic_money"
        isIncome = intent.getBooleanExtra("is_income", false)
        selectedIconResId = resources.getIdentifier(selectedIconName, "drawable", packageName)
            .takeIf { it != 0 } ?: R.drawable.ic_category_default

        // Формат даты
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        dateInput.setText(formatter.format(Date(selectedDateMillis)))

        // Показ даты
        dateInput.setOnClickListener { showDatePickerDialog() }

        // Отображение категории
        selectedCategory?.let {
            categoryLayout.visibility = View.VISIBLE
            categoryName.text = it
            categoryIcon.setImageResource(selectedIconResId)

            val color = CategoryColorHelper.getColorForCategoryKey(selectedIconName, isIncome)
            val bgDrawable = DrawableCompat.wrap(categoryIcon.background.mutate())
            DrawableCompat.setTint(bgDrawable, color)
            categoryIcon.background = bgDrawable
        }

        // Кнопка редактирования категории
        btnEditCategory.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java)
            intent.putExtra("category_id", 0) // TODO: передать ID категории, если доступен
            intent.putExtra("category_name", selectedCategory)
            intent.putExtra("category_icon", selectedIconName)
            intent.putExtra("category_is_income", isIncome)
            startActivity(intent)
        }

        // Если редактируем
        selectedExpenseId = intent.getIntExtra("expense_id", -1).takeIf { it != -1 }
        selectedExpenseId?.let { id ->
            viewModel.getExpenseById(id).observe(this) { expense ->
                if (expense != null) {
                    titleInput.setText(expense.title)
                    amountInput.setText(expense.amount.toString())
                    selectedDateMillis = expense.date
                    dateInput.setText(formatter.format(Date(expense.date)))
                    selectedCategory = expense.category
                    selectedIconName = expense.iconName
                    selectedIconResId = resources.getIdentifier(selectedIconName, "drawable", packageName)
                        .takeIf { it != 0 } ?: R.drawable.ic_category_default

                    categoryName.text = selectedCategory
                    categoryIcon.setImageResource(selectedIconResId)

                    val color = CategoryColorHelper.getColorForCategoryKey(selectedIconName, isIncome)
                    val bgDrawable = DrawableCompat.wrap(categoryIcon.background.mutate())
                    DrawableCompat.setTint(bgDrawable, color)
                    categoryIcon.background = bgDrawable

                    categoryLayout.visibility = View.VISIBLE
                }
            }
        }

        // Сохранение
        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim() // может быть пустым
            val amount = amountInput.text.toString().toDoubleOrNull()

            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expense = Expense(
                id = selectedExpenseId ?: 0,
                title = title,
                amount = amount,
                category = selectedCategory ?: "Категория",
                date = selectedDateMillis,
                note = null,
                type = if (isIncome) "income" else "expense",
                iconName = selectedIconName
            )

            lifecycleScope.launch {
                if (selectedExpenseId != null) {
                    viewModel.update(expense)
                    Snackbar.make(saveButton, "Обновлено", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.insert(expense)
                    Snackbar.make(saveButton, "Сохранено", Snackbar.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDateMillis = calendar.timeInMillis
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
            dateInput.setText(formatter.format(calendar.time))
        }

        DatePickerDialog(
            this, listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}