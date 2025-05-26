package com.example.moneymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    val allCategories: LiveData<List<Category>> = repository.allCategories

    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }

    suspend fun getCategoryById(id: Int): Category? {
        return repository.getCategoryById(id)
    }

    // ✅ Новый метод для получения категорий по типу доход/расход
    fun getCategories(isIncome: Boolean): LiveData<List<Category>> {
        return if (isIncome) {
            repository.getIncomeCategories()
        } else {
            repository.getExpenseCategories()
        }
    }
}

class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}