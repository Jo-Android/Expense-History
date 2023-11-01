package com.hgtech.expensehistory.ui.viewModel

import androidx.lifecycle.ViewModel
import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.dao.ItemDao
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.ItemTable
import kotlinx.coroutines.flow.Flow

class AddUpdateItemViewModel(private val categoryDao: CategoryDao, private val itemDao: ItemDao) :
    ViewModel() {

    suspend fun insertItem(itemTable: ItemTable): Long {
        return itemDao.insertItem(itemTable)
    }

    suspend fun getAllCategories(): Flow<List<CategoryTable>> {
        return categoryDao.getAllCategoriesDistinct()
    }

    suspend fun updateItem(item: ItemTable): Int {
        return itemDao.updateItem(item)
    }

    suspend fun delete(itemTable: ItemTable): Int {
        return itemDao.delete(itemTable)
    }
}