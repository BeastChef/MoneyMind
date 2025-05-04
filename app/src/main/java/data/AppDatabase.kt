package com.example.moneymind.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneymind.data.Category
import com.example.moneymind.data.Expense
import com.example.moneymind.model.CustomCategoryEntity

@Database(
    entities = [Expense::class, Category::class, CustomCategoryEntity::class], // 👈 добавили CustomCategoryEntity
    version = 3, // 👈 увеличили версию БД до 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun customCategoryDao(): CustomCategoryDao // 👈 добавили DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                )
                    .fallbackToDestructiveMigration() // 👈 сбрасывает БД при обновлении схемы
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}