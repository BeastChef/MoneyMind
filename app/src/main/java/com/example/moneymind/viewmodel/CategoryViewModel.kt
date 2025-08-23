package com.example.moneymind.viewmodel

import androidx.lifecycle.*
import com.example.moneymind.data.Category
import com.example.moneymind.data.CategoryRepository
import com.example.moneymind.model.CustomCategoryEntity
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Log


import com.example.moneymind.utils.FirestoreHelper


class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    // ---------- Обычные (дефолтные) категории ----------


    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
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



    fun deleteCustom(category: CustomCategoryEntity) = viewModelScope.launch {
        repository.deleteCustom(category)
    }

    // Универсальный метод для удаления категорий (и кастомных, и обычных)
    fun deleteCategory(category: Any) = viewModelScope.launch {
        when (category) {
            is Category -> repository.delete(category) // Если это обычная категория
            is CustomCategoryEntity -> repository.deleteCustom(category) // Если это кастомная категория
            else -> throw IllegalArgumentException("Unsupported category type")
        }
    }

    fun getCustomCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>> {
        return repository.getCustomCategories(isIncome)
    }
    // Добавляем метод для проверки существования категории по имени и иконке
    suspend fun getCategoryByNameAndIcon(name: String, iconName: String): Category? {
        return repository.getCategoryByNameAndIcon(name, iconName)
    }

    fun syncCategoriesFromFirestore(context: Context) {
        viewModelScope.launch {
            FirestoreHelper.syncCategoriesFromFirestore(context, object : FirestoreHelper.CategorySyncCallback {
                override fun onCategoriesLoaded(categories: List<Category>) {
                    categories.forEach { category ->
                        insert(category) // Вставляем категории в локальную базу данных
                    }
                }

                override fun onError(e: Exception) {
                    Log.e("CategoryViewModel", "Ошибка синхронизации категорий", e)
                }
        })
    }
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

