package com.hgtech.expensehistory.database.repository

import android.util.Log
import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.dao.CurrencyDao
import com.hgtech.expensehistory.database.dao.UserDao
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.CurrencyTable
import com.hgtech.expensehistory.database.model.ItemTable
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.database.relation.ItemCategory
import com.hgtech.expensehistory.di.module.Module
import java.util.Date

class BackupRestoreRepository(
    private val categoryDao: CategoryDao,
    private val userDao: UserDao,
    private val currencyDao: CurrencyDao,
    private val filterRepository: FilterItemsRepository
) {

    suspend fun backup(
        categories: IntArray,
        users: IntArray,
        from: Long?,
        to: Long?,
        onResult: (backup: BackupRestoreGSON) -> Unit
    ) {
        filterRepository.itemDao.getAllItems().also { items ->
            if (items == null)
                onResult.invoke(prepareBackup(listOf()))
            else
                filterRepository.filter(from, to, users, categories, items) {
                    onResult.invoke(prepareBackup(generateItems(it ?: listOf())))
                }
        }
    }


    private fun generateItems(items: List<ItemCategory>): List<Items> {
        return items.map {
            Items(
                0,
                it.itemTable?.category ?: 0,
                it.itemTable?.currency ?: 0,
                it.itemTable?.date?.time,
                it.itemTable?.description ?: "",
                it.itemTable?.total ?: 0.0
            )
        }
    }

    private suspend fun prepareBackup(items: List<Items>): BackupRestoreGSON {
        userDao.getAllUsers().also { users ->
            categoryDao.getAllCategories().also { categories ->
                currencyDao.getAllCurrencies().also { currencies ->
                    return BackupRestoreGSON(
                        users,
                        categories,
                        currencies,
                        items
                    )
                }
            }
        }
    }

    suspend fun restore(file: BackupRestoreGSON) {
        Log.d("TAG", "Object is $file")
        file.usersList?.let { restoreUsers(it) }
        file.categories?.let { restoreCategories(it) }
        file.currency?.let { restoreCurrencies(it) }
        file.items?.let { restoreItems(it) }
    }

    private suspend fun restoreItems(items: List<Items>) {
        items.forEach {
            filterRepository.itemDao.insertItem(
                ItemTable(
                    it.id, it.category, it.currency,
                    it.date?.let { it1 -> Date(it1) }, it.description, it.total
                )
            )
        }
    }

    private suspend fun restoreCurrencies(currency: List<CurrencyTable>) {
        currency.forEach { currencyDao.insert(it) }
    }

    private suspend fun restoreCategories(categories: List<CategoryTable>) {
        categories.forEach { categoryDao.insert(it) }
    }

    private suspend fun restoreUsers(users: List<UserTable>) {
        users.forEach { userDao.insert(it) }
    }

}

data class BackupRestoreGSON(
    val usersList: List<UserTable>?,
    val categories: List<CategoryTable>?,
    val currency: List<CurrencyTable>?,
    val items: List<Items>?,
    val dbVersion: Int = Module.DB_VERSION
)

data class Items(
    val id: Int,
    val category: Int,
    val currency: Int,
    val date: Long?,
    val description: String,
    val total: Double,
)