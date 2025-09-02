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
    fun resetCategories(context: Context) {
        viewModelScope.launch {
            FirestoreHelper.clearAndSyncCategories(context)
        }
    }
    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun getCategories(isIncome: Boolean): LiveData<List<Category>> {
        return repository.getCategoriesByType(isIncome)
    }

    // ---------- Кастомные категории ----------
    fun insertCustom(category: CustomCategoryEntity) = viewModelScope.launch {
        repository.insertCustom(category)
    }

    fun updateCustom(category: CustomCategoryEntity) = viewModelScope.launch {
        repository.updateCustom(category)
    }

    suspend fun getCategoryByUuid(uuid: String): CustomCategoryEntity? {
        return repository.getCustomCategoryByUuid(uuid)
    }

    suspend fun getCategoryByNameAndType(name: String, isIncome: Boolean): CustomCategoryEntity? {
        return repository.getCategoryByNameAndType(name, isIncome)
    }

    fun deleteCustom(category: CustomCategoryEntity) = viewModelScope.launch {
        repository.deleteCustom(category)
    }

    // ✅ ЕДИНЫЙ метод удаления категории (и обычной, и кастомной) + удаление из Firestore
    fun deleteCategory(category: Any) = viewModelScope.launch {
        when (category) {
            is Category -> {
                repository.delete(category)                      // локально (Room)
                FirestoreHelper.deleteCategoryFromFirestore(category) // Firestore (по uuid)
            }
            is CustomCategoryEntity -> {
                repository.deleteCustom(category)                      // локально (Room)
                FirestoreHelper.deleteCustomCategoryFromFirestore(category) // Firestore (по uuid)
            }
            else -> throw IllegalArgumentException("Unsupported category type")
        }
    }

    fun getCustomCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>> {
        return repository.getCustomCategories(isIncome)
    }

    // Проверка существования по имени и иконке (оставляем как есть)
    suspend fun getCategoryByNameAndIcon(name: String, iconName: String): Category? {
        return repository.getCategoryByNameAndIcon(name, iconName)
    }

    fun syncCategoriesFromFirestore(context: Context) {
        viewModelScope.launch {
            FirestoreHelper.syncCategoriesFromFirestore(context, object : FirestoreHelper.CategorySyncCallback {
                override fun onCategoriesLoaded(categories: List<Category>) {
                    categories.forEach { category ->
                        insert(category) // вставка в локальную БД
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