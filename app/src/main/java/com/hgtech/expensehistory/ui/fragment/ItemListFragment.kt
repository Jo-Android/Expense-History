package com.hgtech.expensehistory.ui.fragment

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.relation.ItemCategory
import com.hgtech.expensehistory.database.repository.FilterItems
import com.hgtech.expensehistory.databinding.FragmentListItemBinding
import com.hgtech.expensehistory.ui.adapter.items.DateItemListAdapter
import com.hgtech.expensehistory.ui.manager.format
import com.hgtech.expensehistory.ui.manager.fragment.BaseFragment
import com.hgtech.expensehistory.ui.viewModel.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListFragment : BaseFragment<FragmentListItemBinding>(FragmentListItemBinding::inflate) {

    private val viewModel by viewModel<ItemViewModel>()
    private var filterItems = FilterItems(listOf(), listOf(), listOf())


    private val adapter by lazy {
        DateItemListAdapter(
            onListUpdated = {
                binding.totalExpense.value.text = it.format()
            },
            onItemClicked = {
                it.let { item ->
                    findNavController().navigate(
                        ItemListFragmentDirections.actionItemListFragmentToItemEditFragment(
                            item
                        )
                    )
                }
            })
    }

    override fun observe() {
        Log.d("TAG", "Observing")
        lifecycleScope.launch {
            viewModel.getCurrency().combine(viewModel.getAllItems()) { currency, items ->
                CurrencyItemList(currency, items)
            }.combine(getFilter()) { a: CurrencyItemList, b: FilterItems? ->
                CombineFlow(a, b)
            }.collectLatest {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    it.filterItems?.let { filterItems = it }
                    binding.totalExpense.currency.text = it.currencyItemList.currency
                    withContext(Dispatchers.IO) {
                        viewModel.displayList(it.currencyItemList.list, it.filterItems) {
                            withContext(Dispatchers.Main) { adapter.setItems(it) }
                        }
                    }
                }
            }
        }
    }

    private fun getFilter(): Flow<FilterItems?> {
        findNavController().currentBackStackEntry.let {
            return it?.savedStateHandle?.getStateFlow(
                SortListFragment.FILTER_ITEMS,
                FilterItems(listOf(), listOf(), listOf(), null, null)
            )
                ?: flow { emit(null) }
        }
    }

    private fun goFilter() {
        viewModel.apply {
            findNavController().navigate(
                ItemListFragmentDirections.actionItemListFragmentToSortListFragment(
                    previousCategories = filterItems.selectedCategories.toIntArray(),
                    previousUser = filterItems.user.toIntArray(),
                    from = filterItems.from ?: 0L, to = filterItems.to ?: 0L
                )
            )
        }
    }

    override fun initLayout() {
        binding.apply {

            totalExpense.title.apply {
                text = getString(R.string.total_expense)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.textColor))
            }

            add.setOnClickListener {
                findNavController().navigate(ItemListFragmentDirections.actionItemListFragmentToNewItemFragment())
            }
            list.adapter = adapter
            add.showHide(list)
            topBar.filter.setOnClickListener {
                goFilter()
            }
            topBar.settings.setOnClickListener {
                findNavController().navigate(ItemListFragmentDirections.actionItemListFragmentToSettingsFragment())
            }
        }
    }

    override fun onBackPressed() = Unit

    data class CurrencyItemList(
        val currency: String,
        val list: List<ItemCategory>?
    )

    data class CombineFlow(
        val currencyItemList: CurrencyItemList,
        val filterItems: FilterItems?
    )
}

