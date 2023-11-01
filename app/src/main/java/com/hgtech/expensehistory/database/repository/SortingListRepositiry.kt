package com.hgtech.expensehistory.database.repository

import android.os.Parcelable
import android.util.Log
import androidx.core.util.Pair
import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.dao.UserDao
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

class SortingListRepository(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao
) {

    suspend fun getUsers(): Flow<List<UserTable>> {
        return userDao.getAllUsersDistinct()
    }

    suspend fun getCategories(): Flow<List<CategoryTable>> {
        return categoryDao.getAllCategoriesDistinct()
    }

    fun filterCategories(categoriesList: List<Categories>, user: List<Int>): List<Categories> {
        if (user.isEmpty())
            for (category in categoriesList) {
                category.isHidden = false
            }
        else
            for (category in categoriesList) {
                category.isHidden = user.find { category.category.userId == it } == null
            }
        return categoriesList
    }

    fun generateSelectionList(
        categorySelected: List<CategoryTable>,
        categories: List<Categories>,
        users: List<Int>,
        fromTo: Pair<Long, Long>?
    ): FilterItems {
        val selectedCategories = categorySelected.getIds()
        Log.d("TAG", "Sort Selected iD $selectedCategories")
        return if (categorySelected.isNotEmpty() && users.size <= 1)
            FilterItems(
                selectedCategories,
                users,
                selectedCategories,
                fromTo?.first,
                fromTo?.second
            )
        else if (users.isEmpty())
            FilterItems(
                listOf(),
                listOf(),
                selectedCategories,
                fromTo?.first,
                fromTo?.second
            )
        else
            FilterItems(
                getSelection(categorySelected, categories, users),
                users,
                selectedCategories,
                fromTo?.first,
                fromTo?.second,
            )
    }

    private fun getSelection(
        categorySelected: List<CategoryTable>,
        categories: List<Categories>,
        users: List<Int>
    ): List<Int> {
        val list = ArrayList<Int>()
        users.forEach { userId ->
            getSelectedByUser(userId, categorySelected).also { userCats ->

                Log.d("TAG", "User $userId List IDs $userCats")
                if (userCats.isNotEmpty())
                    list.addAll(userCats)
                else
                    list.addAll(categories.filter { it.category.userId == userId }
                        .map { it.category.id })
            }
        }
        Log.d("TAG", "Final List IDs $list")
        return list.toList()
    }


    private fun getSelectedByUser(
        userId: Int,
        selected: List<CategoryTable>,
    ): List<Int> {
        return selected.filter { it.userId == userId }.getIds()
    }


    private fun List<CategoryTable>.getIds(): List<Int> {
        return map { it.id }
    }
}

data class Categories(
    val category: CategoryTable,
    var isChecked: Boolean = false,
    var isHidden: Boolean = false
)


data class Users(
    val user: UserTable,
    var isChecked: Boolean = false,
)

@Parcelize
data class FilterItems(
    val categories: List<Int>,
    val user: List<Int>,
    val selectedCategories: List<Int>,
    val from: Long? = null,
    val to: Long? = null,
) : Parcelable

@Parcelize
data class CategorySelected(
    val category: Int,
    val user: Int,
) : Parcelable