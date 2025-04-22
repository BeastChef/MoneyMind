package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.CategoryTotal
import com.example.moneymind.data.Expense
import com.example.moneymind.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Все расходы
    val allExpenses: LiveData<List<Expense>> = repository.allExpenses

    // Категории с суммами (все)
    private val _categoryTotals: LiveData<List<CategoryTotal>> = repository.getCategoryTotals()
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> = _categoryTotals

    fun getExpenses(): LiveData<List<Expense>> = allExpenses

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

    // 🔽 Фильтрация по датам (для главного экрана)
    fun getLast7DaysExpenses(): LiveData<List<Expense>> {
        val daysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(daysAgo)
    }

    fun getLast30DaysExpenses(): LiveData<List<Expense>> {
        val daysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(daysAgo)
    }

    // ✅ 🔽 Добавляем недостающие методы
    fun getLast90DaysExpenses(): LiveData<List<Expense>> {
        val daysAgo = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(daysAgo)
    }

    fun getLast365DaysExpenses(): LiveData<List<Expense>> {
        val daysAgo = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
        return repository.getExpensesFromDate(daysAgo)
    }

    // ✅ Статистика по категориям за разные периоды (для диаграмм)
    fun getLast7DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val daysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDate(daysAgo)
    }

    fun getLast30DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val daysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDate(daysAgo)
    }

    fun getLast90DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val daysAgo = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDate(daysAgo)
    }

    fun getLast365DaysCategoryTotals(): LiveData<List<CategoryTotal>> {
        val daysAgo = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
        return repository.getCategoryTotalsFromDate(daysAgo)
    }

    // ✅ Получение расходов по категории (для проваливания)
    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return repository.getExpensesByCategory(category)
    }
}