package com.hgtech.expensehistory.ui.viewModel

import androidx.lifecycle.ViewModel
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.database.repository.CategoryRepository
import com.hgtech.expensehistory.database.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class AddUpdateRootViewModel(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getRootCategory(id: Int): String {
        return categoryRepository.getCategoryName(id)
    }

    suspend fun deleteCategory(id: Int, isRoot: Boolean): Int {
        return categoryRepository.deleteCategory(id, isRoot)
    }

    suspend fun insertCategory(categoryTable: CategoryTable): Long {
        return categoryRepository.insert(categoryTable)
    }

    suspend fun updateCategory(categoryTable: CategoryTable): Int {
        return categoryRepository.update(categoryTable)
    }

    suspend fun getUserName(userId: Int): String {
        return userRepository.getUsername(userId)
    }

    suspend fun getAllUsers(): Flow<List<UserTable>> {
        return userRepository.getAllUsersDistinct()
    }


    suspend fun getAllCategoriesByCategory(id: Int): Flow<List<CategoryTable>> {
        return categoryRepository.getAllCategoriesByCategoryDistinct(id)
    }

    suspend fun getAllCategories(): Flow<List<CategoryTable>> {
        return categoryRepository.getAllCategoriesDistinct()
    }

    suspend fun updateUser(userTable: UserTable): Int {
        return userRepository.update(userTable)
    }

    suspend fun deleteUser(user: UserTable, isRoot: Boolean): Int {
        return userRepository.delete(user, isRoot)
    }

    suspend fun getAllUsersDistinct(id: Int): Flow<List<UserTable>> {
        return userRepository.getAllUsers(id)
    }

    suspend fun insertUser(userTable: UserTable): Long {
        return userRepository.insert(userTable)
    }

}
