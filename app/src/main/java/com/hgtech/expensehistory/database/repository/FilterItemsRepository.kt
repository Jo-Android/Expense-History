package com.hgtech.expensehistory.database.repository

import com.hgtech.expensehistory.database.dao.CurrencyDao
import com.hgtech.expensehistory.database.dao.ItemDao
import com.hgtech.expensehistory.database.relation.ItemCategory
import com.hgtech.expensehistory.ui.manager.getDate
import kotlinx.coroutines.flow.Flow
import java.util.Date

class FilterItemsRepository(
    val itemDao: ItemDao,
    private val currencyDao: CurrencyDao
) {
    suspend fun showResult(
        items: List<ItemCategory>?,
        filterItems: FilterItems?,
        onResult: suspend (list: List<ItemCategory>?) -> Unit,
    ) {
        if (items == null || filterItems == null)
            onResult.invoke(null)
        else
            filterItems.filter(items, onResult)
    }


    suspend fun filter(
        from: Long?,
        to: Long?,
        users: IntArray,
        categories: IntArray,
        items: List<ItemCategory>,
        onResult: suspend (list: List<ItemCategory>?) -> Unit
    ) {
        FilterItems(categories.toList(), users.toList(), listOf(), from, to).filter(items, onResult)
    }

    private suspend fun FilterItems.filter(
        items: List<ItemCategory>,
        onResult: suspend (list: List<ItemCategory>?) -> Unit
    ) {
        if (from != null && from != 0L)
            filterWithDate(items, onResult)
        else
            generateResult(items, categories, user.isNotEmpty(), onResult)
    }

    private suspend fun FilterItems.filterWithDate(
        items: List<ItemCategory>,
        onResult: suspend (list: List<ItemCategory>?) -> Unit,
    ) {
        items.filter {
            (it.itemTable?.date ?: Date(0L)) >= Date(from!!) && (it.itemTable?.date
                ?: Date(0L)) <= Date(to ?: getDate().time)
        }.also {
            generateResult(it, categories, user.isNotEmpty(), onResult)
        }
    }


    private suspend fun generateResult(
        items: List<ItemCategory>?,
        categories: List<Int>,
        hasUser: Boolean,
        onResult: suspend (list: List<ItemCategory>?) -> Unit
    ) {
        if (items == null)
            onResult.invoke(null)
        else if (!hasUser && categories.isEmpty())
            onResult.invoke(items)
        else
            onResult.invoke(sortByCategories(items, categories))
    }

    private fun sortByCategories(
        items: List<ItemCategory>,
        categories: List<Int>
    ): List<ItemCategory> {
        return items.toMutableList()
            .filter { findCategories(it, categories) }
    }

    private fun findCategories(item: ItemCategory, categories: List<Int>): Boolean {
        return (categories.find { it == (item.categoryTable?.id ?: 0) } != null)
    }

    suspend fun getCurrency(): Flow<String> {
        return currencyDao.getCurrency()
    }
}
