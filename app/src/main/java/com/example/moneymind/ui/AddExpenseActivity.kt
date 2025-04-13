package com.example.moneymind.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.moneymind.MoneyMindApp
import com.example.moneymind.R
import com.example.moneymind.data.*
import java.util.*
import com.example.moneymind.viewmodel.ExpenseViewModel
import com.example.moneymind.viewmodel.ExpenseViewModelFactory
class AddExpenseActivity : AppCompatActivity() {

    private lateinit var amountInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var saveButton: Button

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory((application as MoneyMindApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        amountInput = findViewById(R.id.editTextAmount)
        categoryInput = findViewById(R.id.editTextCategory)
        saveButton = findViewById(R.id.buttonSave)

        saveButton.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            val category = categoryInput.text.toString()

            if (amount != null && category.isNotBlank()) {
                val expense = Expense(
                    amount = amount,
                    category = category,
                    date = Date().time,
                    note = null
                )
                viewModel.insert(expense)
                finish()
            }
        }
    }
}