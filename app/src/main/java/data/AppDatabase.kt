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
    version = 5,
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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).categoryDao()
                                // Добавляем дефолтные расходные категории
                                dao.insert(Category(name = "Развлечения", iconResId = R.drawable.ic_entertainment, isIncome = false))
                                dao.insert(Category(name = "Еда", iconResId = R.drawable.ic_food, isIncome = false))
                                dao.insert(Category(name = "Подарки", iconResId = R.drawable.ic_gift, isIncome = false))
                                dao.insert(Category(name = "Медицина", iconResId = R.drawable.ic_medical, isIncome = false))
                                dao.insert(Category(name = "Дом", iconResId = R.drawable.ic_myhome, isIncome = false))
                                dao.insert(Category(name = "Шопинг", iconResId = R.drawable.ic_shopping, isIncome = false))
                                dao.insert(Category(name = "Транспорт", iconResId = R.drawable.ic_transport, isIncome = false))
                                // Добавляем дефолтные доходные категории
                                dao.insert(Category(name = "Зарплата", iconResId = R.drawable.ic_salary, isIncome = true))
                                dao.insert(Category(name = "Инвестиции", iconResId = R.drawable.ic_investments, isIncome = true))
                                dao.insert(Category(name = "Подарки", iconResId = R.drawable.ic_gift, isIncome = true))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}