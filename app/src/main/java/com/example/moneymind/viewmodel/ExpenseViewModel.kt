package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.CategoryTotal
import com.example.moneymind.data.Expense
import com.example.moneymind.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // 🔹 Все записи
    val allExpenses: LiveData<List<Expense>> = repository.allExpenses

    // 🔹 Только расходы
    val allExpensesOnly: LiveData<List<Expense>> = repository.allExpensesOnly

    // 🔹 Только доходы
    val allIncomes: LiveData<List<Expense>> = repository.allIncomes

    // 🔹 Категории (только расходы по умолчанию)
    private val _categoryTotals: LiveData<List<CategoryTotal>> = repository.getCategoryTotalsOnly()
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> = _categoryTotals

    fun getExpenses(): LiveData<List<Expense>> = allExpensesOnly

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    fun getExpenseById(id: Int): LiveData<Expense> {
        return repository.getExpenseById(id)
    }

    // 🔽 Все типы по дате (например, если выбраны "все")
    fun getLast7DaysExpenses(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getAllFromDate(from)
    }

    fun getLast30DaysExpenses(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getAllFromDate(from)
    }

    fun getLast90DaysExpenses(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        return repository.getAllFromDate(from)
    }

    fun getLast365DaysExpenses(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
        return repository.getAllFromDate(from)
    }

    // 🔽 Только расходы по дате
    fun getLast7DaysExpensesOnly(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDateOnly(from)
    }

    fun getLast30DaysExpensesOnly(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDateOnly(from)
    }

    fun getLast90DaysExpensesOnly(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDateOnly(from)
    }

    fun getLast365DaysExpensesOnly(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDateOnly(from)
    }

    // 🔽 Только доходы по дате
    fun getLast7DaysIncomes(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getIncomesFromDate(from)
    }

    fun getLast30DaysIncomes(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getIncomesFromDate(from)
    }

    fun getLast90DaysIncomes(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        return repository.getIncomesFromDate(from)
    }

    fun getLast365DaysIncomes(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
        return repository.getIncomesFromDate(from)
    }

    // 🔽 Категории только расходов по дате
    fun getLast7DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val from = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDateOnly(from)
    }

    fun getLast30DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val from = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDateOnly(from)
    }

    fun getLast90DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val from = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDateOnly(from)
    }

    fun getLast365DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val from = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDateOnly(from)
    }
    fun getLast7DaysAll(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(from) // доходы + расходы
    }

    fun getLast30DaysAll(): LiveData<List<Expense>> {
        val from = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(from)
    }

    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>> {
        return repository.getCategoryTotalsOnly()
    }
    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return repository.getExpensesByCategory(category)
    }
}