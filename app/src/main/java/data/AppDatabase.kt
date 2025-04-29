package com.example.moneymind.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Expense::class, Category::class], // ✅ Expense и Category
    version = 2, // ✅ версия БД увеличена до 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database" // Имя файла базы данных
                )
                    .fallbackToDestructiveMigration() // ✅ если изменится схема — пересоздать БД
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}