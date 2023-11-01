package com.hgtech.expensehistory.database.repository

import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.model.CategoryTable
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao,
) {
    suspend fun getCategoryName(id: Int): String {
        return categoryDao.getCategoryName(id)
    }

    suspend fun insert(categoryTable: CategoryTable): Long {
        return categoryDao.insert(categoryTable)
    }

    suspend fun update(categoryTable: CategoryTable): Int {
        return categoryDao.update(categoryTable)
    }

    suspend fun getAllCategoriesByCategoryDistinct(id: Int): Flow<List<CategoryTable>> {
        return categoryDao.getAllCategoriesByCategoryDistinct(id)
    }

    suspend fun getAllCategoriesDistinct(): Flow<List<CategoryTable>> {
        return categoryDao.getAllCategoriesDistinct()
    }

    suspend fun deleteCategory(id: Int, isRoot: Boolean): Int {
        if (!isRoot)
            return categoryDao.deactivate(id)
        else {
            categoryDao.changeRoot(id).also {
                return if (it > 0)
                    categoryDao.deactivate(id)
                else
                    0
            }
        }
    }

}