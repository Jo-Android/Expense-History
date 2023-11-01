package com.hgtech.expensehistory.ui.viewModel

import androidx.lifecycle.ViewModel
import com.hgtech.expensehistory.database.dao.ItemDao
import com.hgtech.expensehistory.database.relation.ItemCategory
import com.hgtech.expensehistory.database.repository.FilterItems
import com.hgtech.expensehistory.database.repository.FilterItemsRepository
import com.hgtech.expensehistory.ui.manager.getMonth
import kotlinx.coroutines.flow.Flow

class ItemViewModel(private val itemDao: ItemDao, private val repository: FilterItemsRepository) :
    ViewModel() {


    private fun generateAllItems(it: List<ItemCategory>?): MutableMap<Int, ArrayList<ItemCategory>> {
        val map = mutableMapOf<Int, ArrayList<ItemCategory>>()
        if (it != null) {
            for (date in it) {
                val month = getMonth(date.itemTable?.date)
                if (month != null) {
                    if (month in map)
                        map[month]!!.add(date)
                    else
                        map[month] = arrayListOf(date)
                }
            }
        }
        return map
    }

    suspend fun getCurrency(): Flow<String> {
        return repository.getCurrency()
    }


    suspend fun displayList(
        itemCategories: List<ItemCategory>?,
        filterItems: FilterItems?,
        onResult: suspend (list: MutableMap<Int, ArrayList<ItemCategory>>) -> Unit
    ) {
        repository.showResult(itemCategories, filterItems) {
            onResult.invoke(generateAllItems(it))
        }
    }

    suspend fun getAllItems(): Flow<List<ItemCategory>?> {
        return itemDao.getAllItemsDistinct()
    }
}