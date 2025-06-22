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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).categoryDao()
                                // Расходы
                                dao.insert(Category(name = "Развлечения", iconName = "ic_entertainment", iconResId = R.drawable.ic_entertainment, isIncome = false))
                                dao.insert(Category(name = "Еда", iconName = "ic_food", iconResId = R.drawable.ic_food, isIncome = false))
                                dao.insert(Category(name = "Подарки", iconName = "ic_gift", iconResId = R.drawable.ic_gift, isIncome = false))
                                dao.insert(Category(name = "Медицина", iconName = "ic_medical", iconResId = R.drawable.ic_medical, isIncome = false))
                                dao.insert(Category(name = "Дом", iconName = "ic_myhome", iconResId = R.drawable.ic_myhome, isIncome = false))
                                dao.insert(Category(name = "Шопинг", iconName = "ic_shopping", iconResId = R.drawable.ic_shopping, isIncome = false))
                                dao.insert(Category(name = "Транспорт", iconName = "ic_transport", iconResId = R.drawable.ic_transport, isIncome = false))
                                // Доходы
                                dao.insert(Category(name = "Зарплата", iconName = "ic_salary", iconResId = R.drawable.ic_salary, isIncome = true))
                                dao.insert(Category(name = "Инвестиции", iconName = "ic_investments", iconResId = R.drawable.ic_investments, isIncome = true))
                                dao.insert(Category(name = "Подарки", iconName = "ic_gift", iconResId = R.drawable.ic_gift, isIncome = true))
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