package com.hgtech.expensehistory.ui.fragment.settings.category

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.databinding.FragmentUpdateRootBinding
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionEditBinding
import com.hgtech.expensehistory.ui.custom.dialog.AskDialog
import com.hgtech.expensehistory.ui.manager.fragment.modify.BaseRootModifyFragment
import com.hgtech.expensehistory.ui.manager.getText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateCategoryFragment :
    BaseRootModifyFragment<CategoryTable>() {

    private val args by navArgs<UpdateCategoryFragmentArgs>()
    override fun onItemClicked(it: CategoryTable) {
        findNavController().navigate(
            UpdateCategoryFragmentDirections.actionUpdateCategoryFragmentSelf(
                it
            )
        )
    }

    override fun FragmentUpdateRootBinding.initLayout() {
        name.title.text = getString(R.string.name)
        name.value.setHint(getString(R.string.category_name))
        rootCategory.apply {
            root.setOnClickListener { showCategoryMenu() }
            title.loadCategories(value, args.category.id)
            title.text = getString(R.string.root_category)
        }
        rootUser.apply {
            root.loadUsers(value, 0)
            root.setOnClickListener { setupUserMenu() }
            title.text = getString(R.string.user)
        }
        rootChild.setup(getString(R.string.sub_categories))
    }


    override fun askDelete() {
        if (adapter.itemCount == 0)
            AskDialog.delete(requireContext()) {
                if (it)
                    deleteItem(false)
            }
        else
            AskDialog.deleteRoot(requireContext()) {
                if (it)
                    deleteItem(false)
            }
    }

    override fun checkBeforeSaving() {
        binding.apply {
            checkCategoryValidData(args.category.id, isDescription.isChecked, name)
        }
    }

    private fun checkCategoryValidData(
        id: Int = 0,
        shouldHaveDescription: Boolean,
        name: LayoutItemTitleDescriptionEditBinding
    ) {
        if (name.getText().isEmpty()) {
            name.value.error = "Add Name"
            Toast.makeText(requireContext(), getString(R.string.category_name), Toast.LENGTH_LONG)
                .show()
        } else
            CoroutineScope(Dispatchers.IO).launch {
                updateItem(generateItem(id, name, shouldHaveDescription))
            }
    }

    private fun generateItem(
        id: Int = 0,
        name: LayoutItemTitleDescriptionEditBinding,
        shouldHaveDescription: Boolean,
    ): CategoryTable {
        return CategoryTable(
            id, getCategorySelected(), getUserSelected(), name.getText(), shouldHaveDescription
        )
    }

    private suspend fun updateItem(categoryTable: CategoryTable) {
        viewModel.updateCategory(categoryTable).also { saveResponse(false) }
    }

    override fun deleteItem(isRoot: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteCategory(args.category.id, isRoot).also { response ->
                withContext(Dispatchers.Main) {
                    if (response <= 0)
                        getErrorDeleting()
                    else
                        onBackPressed()
                }
            }
        }
    }

    override fun updateLayout(isEnabled: Boolean, shouldNotLoadData: Boolean) {
        with(binding) {
            isDescription.setEnabled(!isEnabled)
            topBar.updateLayout(isEnabled, shouldNotLoadData)
            name.value.isEditEnable(isEnabled)
            rootCategory.root.isVisible =
                !isEnabled || (args.category.rootId != 0 && !shouldNotLoadData) || rootCategory.getText()
                    .isNotEmpty()
            rootChild.root.isVisible = adapter.itemCount > 0 && isEnabled
        }
    }

    override fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getRootCategory(args.category.rootId).also {
                withContext(Dispatchers.Main) { binding.rootCategory.value.text = it }
            }
            viewModel.getUserName(args.category.userId).also {
                withContext(Dispatchers.Main) { binding.rootUser.value.text = it }
            }
        }
        args.category.also {
            binding.name.value.setText(it.title)
            binding.isDescription.isChecked = it.shouldHaveDetail
            updateRoot(it.userId, it.rootId)
        }
    }

    override fun observe() {
        CoroutineScope(Dispatchers.IO).launch {
            var isChanged: Boolean
            viewModel.getAllCategoriesByCategory(args.category.id).collectLatest {
                isChanged = true
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (isChanged) {
                        isChanged = false
                        withContext(Dispatchers.Main) {
                            adapter.addList(it)
                            updateLayout(!isEdit, true)
                        }
                    }
                }
            }
        }
    }
}
