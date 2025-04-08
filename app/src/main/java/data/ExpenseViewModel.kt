package com.example.moneymind.data
import com.example.moneymind.data.Expense
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    val allExpenses: LiveData<List<Expense>> = repository.allExpenses.asLiveData()

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }
}