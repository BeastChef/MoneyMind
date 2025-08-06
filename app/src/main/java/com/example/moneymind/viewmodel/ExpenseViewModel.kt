package com.example.moneymind.viewmodel

import android.util.Log
import com.example.moneymind.utils.FirestoreHelper
import androidx.lifecycle.*
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.data.CategoryTotal
import com.example.moneymind.data.Expense
import com.example.moneymind.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    val allExpenses: LiveData<List<Expense>> = expenseRepository.allExpenses
    val allExpensesOnly: LiveData<List<Expense>> = expenseRepository.allExpensesOnly
    val allIncomes: LiveData<List<Expense>> = expenseRepository.allIncomes

    private val _categoryTotals: LiveData<List<CategoryTotal>> = expenseRepository.getCategoryTotalsOnly()
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> = _categoryTotals

    fun getExpenses(): LiveData<List<Expense>> = allExpensesOnly

    fun insert(expense: Expense) = viewModelScope.launch {
        expenseRepository.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        expenseRepository.update(expense)
        FirestoreHelper.updateExpenseInFirestore(expense)  // 🔥 сохраняем в Firestore
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        expenseRepository.delete(expense)
        FirestoreHelper.deleteExpenseFromFirestore(expense)  // ❌ удаляем из Firestore
    }

    fun getExpenseById(id: Int): LiveData<Expense> {
        return expenseRepository.getExpenseById(id)
    }

    // Методы для работы с периодами (7, 30, 90, 365 дней)
    fun getLast7DaysExpenses(): LiveData<List<Expense>> = expenseRepository.getAllFromDate(daysAgo(7))
    fun getLast30DaysExpenses(): LiveData<List<Expense>> = expenseRepository.getAllFromDate(daysAgo(30))
    fun getLast90DaysExpenses(): LiveData<List<Expense>> = expenseRepository.getAllFromDate(daysAgo(90))
    fun getLast365DaysExpenses(): LiveData<List<Expense>> = expenseRepository.getAllFromDate(daysAgo(365))

    fun getLast7DaysExpensesOnly(): LiveData<List<Expense>> = expenseRepository.getExpensesFromDateOnly(daysAgo(7))
    fun getLast30DaysExpensesOnly(): LiveData<List<Expense>> = expenseRepository.getExpensesFromDateOnly(daysAgo(30))
    fun getLast90DaysExpensesOnly(): LiveData<List<Expense>> = expenseRepository.getExpensesFromDateOnly(daysAgo(90))
    fun getLast365DaysExpensesOnly(): LiveData<List<Expense>> = expenseRepository.getExpensesFromDateOnly(daysAgo(365))

    fun getLast7DaysIncomes(): LiveData<List<Expense>> = expenseRepository.getIncomesFromDate(daysAgo(7))
    fun getLast30DaysIncomes(): LiveData<List<Expense>> = expenseRepository.getIncomesFromDate(daysAgo(30))
    fun getLast90DaysIncomes(): LiveData<List<Expense>> = expenseRepository.getIncomesFromDate(daysAgo(90))
    fun getLast365DaysIncomes(): LiveData<List<Expense>> = expenseRepository.getIncomesFromDate(daysAgo(365))

    // Методы для работы с категориями
    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>> = expenseRepository.getCategoryTotalsOnly()
    fun getExpensesByCategory(category: String): LiveData<List<Expense>> = expenseRepository.getExpensesByCategory(category)

    // Методы для работы с произвольным диапазоном дат (для календаря)
    fun getExpensesBetween(startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return expenseRepository.getExpensesBetweenDates(startDate, endDate)
    }

    fun getExpensesByExactDate(date: Long): LiveData<List<Expense>> {
        return expenseRepository.getExpensesByExactDate(date)
    }

    // Методы для поиска по названию и категории
    fun searchExpensesByTitle(query: String): LiveData<List<Expense>> {
        return expenseRepository.searchExpensesByTitle(query)
    }

    fun searchExpensesByTitleOrCategory(query: String): LiveData<List<Expense>> {
        return expenseRepository.searchExpensesByTitleOrCategory(query)
    }

    // Вставка/обновление категорий
    fun insertExpense(expense: Expense) = viewModelScope.launch {
        expenseRepository.insert(expense)
        FirestoreHelper.saveExpenseToFirestore(expense)
        Log.d("Firestore", "Saving expense and category to Firestore...");
    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        categoryRepository.insert(category)
        FirestoreHelper.saveCategoryToFirestore(category)
        Log.d("Firestore", "Saving expense and category to Firestore...");
    }

    // Метод для получения всех расходов и категорий
    fun getAllExpensesData(): LiveData<List<Expense>> = expenseRepository.allExpenses

    fun getAllCategories(): LiveData<List<Category>> {
        return categoryRepository.allCategories
    }

    // Методы для работы с последними расходами (все за последние 7, 30 дней)
    fun getLast7DaysAll(): LiveData<List<Expense>> = expenseRepository.getExpensesFromDate(daysAgo(7))
    fun getLast30DaysAll(): LiveData<List<Expense>> = expenseRepository.getExpensesFromDate(daysAgo(30))

    // Утилита для вычисления времени в прошлом
    private fun daysAgo(days: Int): Long {
        return System.currentTimeMillis() - days * 24L * 60 * 60 * 1000
    }
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return expenseRepository.getExpensesBetweenDates(startDate, endDate)
    }

    // Метод восстановления данных с Firebase
    @JvmOverloads
    fun restoreFromFirebase() {
        FirestoreHelper.loadCategoriesFromFirestore(object : FirestoreHelper.CategoryDataCallback {
            override fun onCategoriesLoaded(categories: List<Category>) {
                viewModelScope.launch {
                    for (category in categories) {
                        categoryRepository.insert(category)
                    }
                }
            }

            override fun onIncomeCategoriesLoaded(incomeCategories: List<Category>) {
                // Обработать доходные категории
                viewModelScope.launch {
                    for (category in incomeCategories) {
                        categoryRepository.insert(category)  // Вставляем доходные категории
                    }
                }
            }

            override fun onExpenseCategoriesLoaded(expenseCategories: List<Category>) {
                // Обработать расходные категории
                viewModelScope.launch {
                    for (category in expenseCategories) {
                        categoryRepository.insert(category)  // Вставляем расходные категории
                    }
                }
            }

            override fun onError(e: Exception) {
                // Обработать ошибку
                Log.e("FirestoreHelper", "Error loading categories: ${e.message}")
            }
        })

        FirestoreHelper.loadExpensesFromFirestore(object : FirestoreHelper.ExpenseDataCallback {
            override fun onExpensesLoaded(expenses: List<Expense>) {
                viewModelScope.launch {
                    for (expense in expenses) {
                        expenseRepository.insert(expense)
                    }
                }
            }

            override fun onError(e: Exception) {
                Log.e("FirestoreHelper", "Error loading expenses: ${e.message}")
            }
        })
    }


}