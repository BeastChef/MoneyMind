package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.CategoryTotal
import com.example.moneymind.data.Expense
import com.example.moneymind.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Все расходы
    val allExpenses: LiveData<List<Expense>> = repository.allExpenses

    // Категории с суммами
    private val _categoryTotals: LiveData<List<CategoryTotal>> = repository.getCategoryTotals()

    // Получить все
    fun getExpenses(): LiveData<List<Expense>> = allExpenses

    // Получить суммы по категориям
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> = _categoryTotals

    // Добавить расход
    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    // Обновить расход
    fun update(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    // Удалить расход
    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    // Получить конкретный расход по ID
    fun getExpenseById(id: Int): LiveData<Expense> {
        return repository.getExpenseById(id)
    }

    // За 7 дней
    fun getLast7DaysExpenses(): LiveData<List<Expense>> {
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(sevenDaysAgo)
    }

    // За 30 дней
    fun getLast30DaysExpenses(): LiveData<List<Expense>> {
        val thirtyDaysAgo = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(thirtyDaysAgo)
    }
}