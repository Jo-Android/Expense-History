package com.hgtech.expensehistory.ui.viewModel

import androidx.lifecycle.ViewModel
import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.dao.UserDao
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import kotlinx.coroutines.flow.Flow

class UserCategoryViewModel(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao
) : ViewModel() {


    suspend fun getUsers(): Flow<List<UserTable>> {
        return userDao.getAllUsersDistinct()
    }

    suspend fun getCategories(): Flow<List<CategoryTable>> {
        return categoryDao.getAllCategoriesDistinct()
    }

    suspend fun insertCategory(categoryTable: CategoryTable): Long {
        return categoryDao.insert(categoryTable)
    }

    suspend fun updateCategory(categoryTable: CategoryTable): Int {
        return categoryDao.update(categoryTable)
    }

    suspend fun insertUser(userTable: UserTable): Long {
        return userDao.insert(userTable)
    }

    suspend fun updateUser(userTable: UserTable): Int {
        return userDao.update(userTable)
    }

}