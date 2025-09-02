package com.example.moneymind.data
import com.example.moneymind.utils.BoolCallback   // ✅ добавь импорт
import androidx.lifecycle.LiveData
import com.example.moneymind.model.CustomCategoryEntity
import com.example.moneymind.utils.FirestoreHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val customCategoryDao: CustomCategoryDao
) {

    // ---------- Обычные (дефолтные) категории ----------

    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }


    // Метод для получения категории по имени и иконке
    suspend fun getCategoryByNameAndIcon(name: String, iconName: String): Category? {
        return categoryDao.getCategoryByNameAndIcon(name, iconName)
    }

    fun getCategoriesByType(isIncome: Boolean): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(isIncome)
    }

    // ---------- Кастомные категории (CustomCategoryEntity) ----------

    suspend fun insertCustom(category: CustomCategoryEntity) {
        customCategoryDao.insert(category)
    }

    suspend fun updateCustom(category: CustomCategoryEntity) {
        customCategoryDao.update(category)
    }

    suspend fun deleteCustom(category: CustomCategoryEntity) {
        customCategoryDao.delete(category)
    }

    suspend fun getCustomCategoryByUuid(uuid: String): CustomCategoryEntity? {
        return customCategoryDao.getCustomCategoryByUuid(uuid)
    }

    suspend fun getCategoryByNameAndType(name: String, isIncome: Boolean): CustomCategoryEntity? {
        return customCategoryDao.getCategoryByNameAndType(name, isIncome)
    }

    fun getCustomCategories(isIncome: Boolean): LiveData<List<CustomCategoryEntity>> {
        return customCategoryDao.getCategories(isIncome)
    }
// ---------- Синхронизация категорий с Firestore ----------

    // ✅ Оставляем Kotlin-версию (для ViewModel и корутин)
    fun syncCategoriesFromFirestore(onComplete: (Boolean) -> Unit) {
        FirestoreHelper.loadCategoriesFromFirestore(object : FirestoreHelper.CategoryDataCallback {
            override fun onCategoriesLoaded(categories: List<Category>) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        categoryDao.deleteAll()
                        categoryDao.insertAll(categories)
                        onComplete(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onComplete(false)
                    }
                }
            }

            override fun onIncomeCategoriesLoaded(income: List<Category>) { /* not used */ }
            override fun onExpenseCategoriesLoaded(expense: List<Category>) { /* not used */ }
            override fun onError(e: Exception) { onComplete(false) }
        })
    }

    // ✅ Добавляем перегрузку для Java (MainActivity.java сможет вызвать этот метод)
    fun syncCategoriesFromFirestore(onComplete: BoolCallback) {
        syncCategoriesFromFirestore { success ->
            onComplete.onResult(success)
        }
    }

}