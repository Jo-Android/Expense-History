package com.hgtech.expensehistory.ui.viewModel

import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.database.repository.Categories
import com.hgtech.expensehistory.database.repository.FilterItems
import com.hgtech.expensehistory.database.repository.SortingListRepository
import kotlinx.coroutines.flow.Flow

class SortListViewModel(private val repository: SortingListRepository) : ViewModel() {

    fun filterCategories(categories: List<Categories>, users: List<Int>) {
        repository.filterCategories(categories, users)
    }

    fun getSelectionList(
        categorySelected: List<CategoryTable>,
        categories: List<Categories>,
        users: List<Int>,
        fromTo: Pair<Long, Long>?,
    ): FilterItems {
        return repository.generateSelectionList(categorySelected, categories, users, fromTo).also {
            Log.d("TAG", "Filter item is $it")
        }
    }

    suspend fun getUser(): Flow<List<UserTable>> {
        return repository.getUsers()
    }

    suspend fun getCategories(): Flow<List<CategoryTable>> {
        return repository.getCategories()
    }
}