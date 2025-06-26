package com.example.moneymind.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moneymind.R
import com.example.moneymind.model.CustomCategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Expense::class, Category::class, CustomCategoryEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun customCategoryDao(): CustomCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).categoryDao()
                                if (dao.getCategoryCount() == 0) {
                                    populateDefaults(dao, context)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDefaults(categoryDao: CategoryDao, context: Context) {
            categoryDao.insertAll(
                listOf(
                    // Доходы
                    Category(name = "Зарплата", iconResId = R.drawable.ic_salary, iconName = "ic_salary", isIncome = true),
                    Category(name = "Инвестиции", iconResId = R.drawable.ic_investments, iconName = "ic_investments", isIncome = true),
                    Category(name = "Подарок", iconResId = R.drawable.ic_gift, iconName = "ic_gift", isIncome = true),

                    // Расходы
                    Category(name = "Продукты", iconResId = R.drawable.ic_food, iconName = "ic_food", isIncome = false),
                    Category(name = "Транспорт", iconResId = R.drawable.ic_transport, iconName = "ic_transport", isIncome = false),
                    Category(name = "Жильё", iconResId = R.drawable.ic_home, iconName = "ic_home", isIncome = false),
                    Category(name = "Связь", iconResId = R.drawable.ic_phone, iconName = "ic_phone", isIncome = false),
                    Category(name = "Одежда", iconResId = R.drawable.ic_clothes, iconName = "ic_clothes", isIncome = false),
                    Category(name = "Развлечения", iconResId = R.drawable.ic_entertainment, iconName = "ic_entertainment", isIncome = false)
                )
            )
        }
    }
}