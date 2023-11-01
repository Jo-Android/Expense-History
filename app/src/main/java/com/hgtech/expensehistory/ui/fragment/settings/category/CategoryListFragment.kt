package com.hgtech.expensehistory.ui.fragment.settings.category

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.ui.manager.fragment.BaseRootListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryListFragment : BaseRootListFragment<CategoryTable>(true) {
    override fun onItemSelected(it: CategoryTable) {
        findNavController().navigate(
            CategoryListFragmentDirections.actionCategoryListFragmentToUpdateCategoryFragment(
                it
            )
        )
    }

    override fun observe() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getCategories().collectLatest {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    withContext(Dispatchers.Main) { adapter.addList(it) }
                }
            }
        }
    }

    override fun onAddClicked() {
        findNavController().navigate(CategoryListFragmentDirections.actionCategoryListFragmentToNewCategoryFragment())
    }
}