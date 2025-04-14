package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.Expense
import com.example.moneymind.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val allExpenses: LiveData<List<Expense>> = repository.allExpenses.asLiveData()

    fun getExpenses(): LiveData<List<Expense>> = allExpenses

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }
}