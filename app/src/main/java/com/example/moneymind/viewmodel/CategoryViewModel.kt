package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.model.CustomCategoryEntity
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    // ---------- Обычные (дефолтные) категории ----------

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

    fun getCategories(isIncome: Boolean): LiveData<List<Category>> {
        return repository.getCategoriesByType(isIncome)
    }

    // ---------- Кастомные категории (CustomCategoryEntity) ----------

    fun insertCustom(category: CustomCategoryEntity) = viewModelScope.launch {
        repository.insertCustom(category)
    }

    fun updateCustom(category: CustomCategoryEntity) = viewModelScope.launch {
        repository.updateCustom(category)
    }

    fun deleteCustomById(id: Int) = viewModelScope.launch {
        repository.deleteCustomById(id)
    }

    fun getCustomCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>> {
        return repository.getCustomCategories(isIncome)
    }
}

class CategoryViewModelFactory(
    private val repository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}