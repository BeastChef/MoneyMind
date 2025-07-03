package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.CategoryTotal
import com.example.moneymind.data.Expense
import com.example.moneymind.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    val allExpenses: LiveData<List<Expense>> = repository.allExpenses
    val allExpensesOnly: LiveData<List<Expense>> = repository.allExpensesOnly
    val allIncomes: LiveData<List<Expense>> = repository.allIncomes

    private val _categoryTotals: LiveData<List<CategoryTotal>> = repository.getCategoryTotalsOnly()
    fun getCategoryTotals(): LiveData<List<CategoryTotal>> = _categoryTotals

    fun getExpenses(): LiveData<List<Expense>> = allExpensesOnly

    fun insert(expense: Expense) = viewModelScope.launch { repository.insert(expense) }
    fun update(expense: Expense) = viewModelScope.launch { repository.update(expense) }
    fun delete(expense: Expense) = viewModelScope.launch { repository.delete(expense) }

    fun getExpenseById(id: Int): LiveData<Expense> {
        return repository.getExpenseById(id)
    }

    // 🔹 Работа с периодами (7, 30, 90, 365 дней) — оставляем
    fun getLast7DaysExpenses(): LiveData<List<Expense>> = repository.getAllFromDate(daysAgo(7))
    fun getLast30DaysExpenses(): LiveData<List<Expense>> = repository.getAllFromDate(daysAgo(30))
    fun getLast90DaysExpenses(): LiveData<List<Expense>> = repository.getAllFromDate(daysAgo(90))
    fun getLast365DaysExpenses(): LiveData<List<Expense>> = repository.getAllFromDate(daysAgo(365))

    fun getLast7DaysExpensesOnly(): LiveData<List<Expense>> = repository.getExpensesFromDateOnly(daysAgo(7))
    fun getLast30DaysExpensesOnly(): LiveData<List<Expense>> = repository.getExpensesFromDateOnly(daysAgo(30))
    fun getLast90DaysExpensesOnly(): LiveData<List<Expense>> = repository.getExpensesFromDateOnly(daysAgo(90))
    fun getLast365DaysExpensesOnly(): LiveData<List<Expense>> = repository.getExpensesFromDateOnly(daysAgo(365))

    fun getLast7DaysIncomes(): LiveData<List<Expense>> = repository.getIncomesFromDate(daysAgo(7))
    fun getLast30DaysIncomes(): LiveData<List<Expense>> = repository.getIncomesFromDate(daysAgo(30))
    fun getLast90DaysIncomes(): LiveData<List<Expense>> = repository.getIncomesFromDate(daysAgo(90))
    fun getLast365DaysIncomes(): LiveData<List<Expense>> = repository.getIncomesFromDate(daysAgo(365))

    fun getLast7DaysCategoryTotals(): LiveData<List<CategoryTotal>> = repository.getCategoryTotalsFromDateOnly(daysAgo(7))
    fun getLast30DaysCategoryTotals(): LiveData<List<CategoryTotal>> = repository.getCategoryTotalsFromDateOnly(daysAgo(30))
    fun getLast90DaysCategoryTotals(): LiveData<List<CategoryTotal>> = repository.getCategoryTotalsFromDateOnly(daysAgo(90))
    fun getLast365DaysCategoryTotals(): LiveData<List<CategoryTotal>> = repository.getCategoryTotalsFromDateOnly(daysAgo(365))

    fun getLast7DaysAll(): LiveData<List<Expense>> = repository.getExpensesFromDate(daysAgo(7))
    fun getLast30DaysAll(): LiveData<List<Expense>> = repository.getExpensesFromDate(daysAgo(30))

    fun getCategoryTotalsOnly(): LiveData<List<CategoryTotal>> = repository.getCategoryTotalsOnly()
    fun getExpensesByCategory(category: String): LiveData<List<Expense>> = repository.getExpensesByCategory(category)

    // 🔥 Новая функция для произвольного диапазона дат (для календаря)
    fun getExpensesBetween(startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return repository.getExpensesBetweenDates(startDate, endDate)
    }

    fun getExpensesByExactDate(date: Long): LiveData<List<Expense>> {
        return repository.getExpensesByExactDate(date)
    }
    fun searchExpensesByTitle(query: String): LiveData<List<Expense>> {
        return repository.searchExpensesByTitle(query)
    }
    fun searchExpensesByTitleOrCategory(query: String): LiveData<List<Expense>> {
        return repository.searchExpensesByTitleOrCategory(query)
    }
    private fun daysAgo(days: Int): Long {
        return System.currentTimeMillis() - days * 24L * 60 * 60 * 1000

    }
    fun getExpensesLast7Days(): LiveData<List<Expense>> {
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
        return repository.getExpensesBetweenDates(sevenDaysAgo, System.currentTimeMillis())
    }

    fun getExpensesLast30Days(): LiveData<List<Expense>> {
        val thirtyDaysAgo = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000
        return repository.getExpensesBetweenDates(thirtyDaysAgo, System.currentTimeMillis())
    }
    fun getExpensesBetweenDates(start: Long, end: Long): LiveData<List<Expense>> {
        return repository.getExpensesBetweenDates(start, end)
    }
}