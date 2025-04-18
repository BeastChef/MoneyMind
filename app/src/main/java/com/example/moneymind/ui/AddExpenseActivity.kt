package com.example.moneymind.ui

import android.app.DatePickerDialog
import android.os.Bundle
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

    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var selectedExpenseId: Int? = null

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

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateInput.setText(formatter.format(calendar.time))

        // Выбор даты
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

        // Если редактируем
        val expenseId = intent.getIntExtra("expense_id", -1)
        if (expenseId != -1) {
            selectedExpenseId = expenseId
            viewModel.getExpenseById(expenseId).observe(this) { expense ->
                if (expense != null) {
                    titleInput.setText(expense.note ?: expense.category)
                    amountInput.setText(expense.amount.toString())
                    selectedDateMillis = expense.date
                    dateInput.setText(formatter.format(Date(expense.date)))
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

            val category = CategoryClassifier.classify(title)

            val expense = Expense(
                id = selectedExpenseId ?: 0,
                amount = amount,
                category = category,
                date = selectedDateMillis,
                note = title
            )

            if (selectedExpenseId != null) {
                viewModel.update(expense)
                Toast.makeText(this, "Расход обновлён", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insert(expense)
                Toast.makeText(this, "Расход добавлен", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}