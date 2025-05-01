package com.example.moneymind.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymind.MoneyMindApp
import com.example.moneymind.R
import com.example.moneymind.data.Expense
import com.example.moneymind.utils.CategoryClassifier
import com.example.moneymind.viewmodel.ExpenseViewModel
import com.example.moneymind.viewmodel.ExpenseViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var titleInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var saveButton: MaterialButton

    private lateinit var categoryLayout: View
    private lateinit var categoryIcon: ImageView
    private lateinit var categoryText: TextView

    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var selectedExpenseId: Int? = null
    private var selectedType: String = "expense"
    private var selectedCategory: String? = null

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory((application as MoneyMindApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        titleInput = findViewById(R.id.inputTitle)
        amountInput = findViewById(R.id.inputAmount)
        dateInput = findViewById(R.id.inputDate)
        saveButton = findViewById(R.id.saveButton)

        categoryLayout = findViewById(R.id.selectedCategoryLayout)
        categoryIcon = findViewById(R.id.selectedCategoryIcon)
        categoryText = findViewById(R.id.selectedCategoryText)

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateInput.setText(formatter.format(calendar.time))

        selectedType = if (intent.getBooleanExtra("is_income", false)) "income" else "expense"
        selectedCategory = intent.getStringExtra("selected_category")

        // Отображение иконки и текста категории (если выбрана)
        val iconMap = mapOf(
            "Зарплата" to R.drawable.ic_salary,
            "Дивиденды" to R.drawable.ic_investments,
            "Подарки" to R.drawable.ic_gift,
            "Другое" to R.drawable.ic_other_income,
            "Еда" to R.drawable.ic_food,
            "Транспорт" to R.drawable.ic_transport,
            "Медицина" to R.drawable.ic_medical,
            "Развлечения" to R.drawable.ic_entertainment,
            "Жильё" to R.drawable.ic_myhome,
            "Покупки" to R.drawable.ic_shopping,
            "Другое" to R.drawable.ic_other
        )

        selectedCategory?.let { name ->
            categoryText.text = name
            val iconRes = iconMap[name] ?: R.drawable.ic_default_category
            categoryIcon.setImageResource(iconRes)
            categoryLayout.visibility = View.VISIBLE
        }

        dateInput.setOnClickListener {
            val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDateMillis = calendar.timeInMillis
                dateInput.setText(formatter.format(calendar.time))
            }

            DatePickerDialog(
                this,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val expenseId = intent.getIntExtra("expense_id", -1)
        if (expenseId != -1) {
            selectedExpenseId = expenseId
            viewModel.getExpenseById(expenseId).observe(this) { expense ->
                if (expense != null) {
                    titleInput.setText(expense.title)
                    amountInput.setText(expense.amount.toString())
                    selectedDateMillis = expense.date
                    dateInput.setText(formatter.format(Date(expense.date)))
                    selectedType = expense.type
                    selectedCategory = expense.category

                    // Показать при редактировании
                    categoryText.text = selectedCategory
                    val iconRes = iconMap[selectedCategory] ?: R.drawable.ic_default_category
                    categoryIcon.setImageResource(iconRes)
                    categoryLayout.visibility = View.VISIBLE
                }
            }
        }

        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if (title.isBlank() || amount == null) {
                Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = selectedCategory ?: CategoryClassifier.classify(this, title)

            val expense = Expense(
                id = selectedExpenseId ?: 0,
                title = title,
                amount = amount,
                category = category,
                date = selectedDateMillis,
                note = null,
                type = selectedType
            )

            if (selectedExpenseId != null) {
                viewModel.update(expense)
                Snackbar.make(saveButton, "Запись обновлена", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.insert(expense)
                Snackbar.make(saveButton, "Запись добавлена", Snackbar.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}