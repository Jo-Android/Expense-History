package com.hgtech.expensehistory.ui.manager.fragment

import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.databinding.FragmentUserCategoryListBinding
import com.hgtech.expensehistory.databinding.LayoutSettingsTopBinding
import com.hgtech.expensehistory.ui.manager.adapter.UserCategoryListAdapter
import com.hgtech.expensehistory.ui.viewModel.UserCategoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseRootListFragment<T>(
    private val isCategory: Boolean = false
) :
    BaseFragment<FragmentUserCategoryListBinding>(FragmentUserCategoryListBinding::inflate) {

    val adapter by lazy {
        UserCategoryListAdapter<T> {
            onItemSelected(it)
        }
    }

    abstract fun onItemSelected(it: T)

    val viewModel by viewModel<UserCategoryViewModel>()

    override fun observe() = Unit


    override fun initLayout() {
        binding.apply {
            topBar.setup()
            listItem.adapter = adapter
            add.showHide(listItem)
            add.setOnClickListener {
                onAddClicked()
            }
        }
    }

    abstract fun onAddClicked()

    override fun onBackPressed() = Unit

    private fun LayoutSettingsTopBinding.setup() {
        backButton.setOnClickListener {
            handleOnBackPressed()
        }
        name.text = getString(if (isCategory) R.string.categories else R.string.users)
    }
}
