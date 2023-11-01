package com.hgtech.expensehistory.database.config

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hgtech.expensehistory.database.converter.Converters
import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.dao.CurrencyDao
import com.hgtech.expensehistory.database.dao.ItemDao
import com.hgtech.expensehistory.database.dao.UserDao
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.CurrencyTable
import com.hgtech.expensehistory.database.model.ItemTable
import com.hgtech.expensehistory.database.model.UserTable

@Database(
    entities = [ItemTable::class, CategoryTable::class, CurrencyTable::class, UserTable::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val itemDao: ItemDao
    abstract val categoryDao:CategoryDao
    abstract val currencyDao:CurrencyDao
    abstract val userDao: UserDao
}