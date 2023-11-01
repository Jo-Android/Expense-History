package com.hgtech.expensehistory.ui.fragment

import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.repository.FilterItems
import com.hgtech.expensehistory.databinding.FragmentNewItemBinding
import com.hgtech.expensehistory.ui.manager.fragment.modify.BaseItemModifyFragment
import com.hgtech.expensehistory.ui.manager.setupDatePicker


class NewItemFragment :
    BaseItemModifyFragment<FragmentNewItemBinding>(FragmentNewItemBinding::inflate) {

    companion object {
        const val TAG = "NewItemFragment"
    }

    override fun onExit() {
        Log.d("TAG", "Exiting New Item")
        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                SortListFragment.FILTER_ITEMS, FilterItems(
                    listOf(), listOf(), listOf(), null, null
                )
            )
        }
    }

    override fun initLayout() {
        isEdit = true
        binding.apply {
            date.root.setOnClickListener { activity?.let { setupDatePicker(it, date.value) } }
            date.title.text = getString(R.string.date_title)
            category.title.text = getString(R.string.category_title)
            description.title.isVisible = false
            description.value.setHint(getString(R.string.enter_description))
            description.value.isEditEnable(false)

            category.root.apply {
                loadCategories(binding.category.value, binding.description)
                setOnClickListener {
                    showCategoryMenu()
                }
            }
            save.setOnClickListener {
                checkValidData(0, date.value, description, total)
            }
        }
    }

    override fun observe() {

    }
}


