package com.example.moneymind.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneymind.MoneyMindApp
import com.example.moneymind.R
import com.example.moneymind.data.Expense
import com.example.moneymind.utils.CategoryColorHelper
import com.example.moneymind.utils.FirestoreHelper
import com.example.moneymind.viewmodel.ExpenseViewModel
import com.example.moneymind.viewmodel.ExpenseViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : BaseActivityK() {

    companion object {
        private const val EDIT_CATEGORY_REQUEST_CODE = 1001
    }


    private lateinit var titleInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private var cancelButton: MaterialButton? = null
    private var categoryIcon: ImageView? = null
    private var categoryName: TextView? = null
    private var btnEditCategory: MaterialButton? = null
    private var categoryLayout: View? = null

    private var selectedCategory: String? = null
    private var selectedIconName: String = "ic_money"
    private var selectedIconResId: Int = R.drawable.ic_category_default
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var isIncome: Boolean = false
    private var selectedExpenseId: Int? = null
    private var selectedCategoryId: Int = -1
    private var isCustomCategory: Boolean = true
    private var isFromMainTab: Boolean = false

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(
            (application as MoneyMindApp).expenseRepository,  // передаем ExpenseRepository
            (application as MoneyMindApp).categoryRepository, // передаем CategoryRepository
            application // Передаем Application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onCreate(savedInstanceState)


        isFromMainTab = intent.getBooleanExtra("from_main_tab", false)
        setContentView(if (isFromMainTab) R.layout.activity_edit_expense_simple else R.layout.activity_add_expense)

        if (!isFromMainTab) {
            val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        titleInput = findViewById(R.id.inputTitle)
        amountInput = findViewById(R.id.inputAmount)
        dateInput = findViewById(R.id.inputDate)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        categoryIcon = findViewById(R.id.selectedCategoryIcon)
        categoryName = findViewById(R.id.selectedCategoryText)
        btnEditCategory = findViewById(R.id.btnEditCategory)
        categoryLayout = findViewById(R.id.selectedCategoryLayout)

        selectedCategory = intent.getStringExtra("selected_category")
        selectedIconName = intent.getStringExtra("selected_icon") ?: "ic_money"
        selectedCategoryId = intent.getIntExtra("category_id", -1)
        isIncome = intent.getBooleanExtra("is_income", false)
        isCustomCategory = intent.getBooleanExtra("is_custom", true)

        selectedIconResId = resources.getIdentifier(selectedIconName, "drawable", packageName)
            .takeIf { it != 0 } ?: R.drawable.ic_category_default

        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateInput.setText(formatter.format(Date(selectedDateMillis)))
        dateInput.setOnClickListener { showDatePickerDialog() }

        selectedCategory?.let { showCategoryDetails() }

        btnEditCategory?.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java).apply {
                putExtra("category_id", selectedCategoryId)
                putExtra("category_name", selectedCategory)
                putExtra("category_icon", selectedIconName)
                putExtra("category_is_income", isIncome)
                putExtra("is_custom", isCustomCategory)
                putExtra("from_main_tab", true)
            }
            startActivityForResult(intent, EDIT_CATEGORY_REQUEST_CODE)
        }

        cancelButton?.setOnClickListener { finish() }

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
                    selectedIconResId = resources.getIdentifier(
                        selectedIconName, "drawable", packageName
                    ).takeIf { it != 0 } ?: R.drawable.ic_category_default

                    showCategoryDetails()
                }
            }
        }

        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if (amount == null || amount <= 0) {
                Toast.makeText(this, getString(R.string.enter_valid_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val color = CategoryColorHelper.getColorForCategoryKey(selectedIconName, isIncome)

            val expense = Expense(
                id = selectedExpenseId ?: 0,
                title = title,
                amount = amount,
                category = selectedCategory ?: getString(R.string.category),
                date = selectedDateMillis,
                type = if (isIncome) "income" else "expense",
                iconName = selectedIconName,
                categoryColor = color
            )

            lifecycleScope.launch {
                if (selectedExpenseId != null) {
                    viewModel.update(expense)
                    Snackbar.make(saveButton, getString(R.string.updated), Snackbar.LENGTH_SHORT).show()

                } else {
                    viewModel.insert(expense)
                    FirestoreHelper.saveExpenseToFirestore(expense)
                    Snackbar.make(saveButton, getString(R.string.saved), Snackbar.LENGTH_SHORT).show()

                }
                finish()
            }
        }
    }


    private fun showCategoryDetails() {
        categoryLayout?.visibility = View.VISIBLE
        categoryName?.text = selectedCategory
        categoryIcon?.setImageResource(selectedIconResId)
        val color = CategoryColorHelper.getColorForCategoryKey(selectedIconName, isIncome)
        categoryIcon?.background?.mutate()?.let { bg ->
            DrawableCompat.setTint(DrawableCompat.wrap(bg), color)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val type = data.getStringExtra("deleted_type") ?: data.getStringExtra("edited_type")
            if (type != null) {
                Toast.makeText(this, getString(R.string.category_updated), Toast.LENGTH_SHORT).show()
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
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            dateInput.setText(formatter.format(calendar.time))
        }

        DatePickerDialog(
            this, listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        val lang = newBase.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
            .getString("app_lang", "ru") ?: "ru"
        val context = com.example.moneymind.utils.LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}