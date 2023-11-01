package com.hgtech.expensehistory.ui.manager.fragment.modify

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.databinding.FragmentUpdateRootBinding
import com.hgtech.expensehistory.databinding.LayoutChildListBinding
import com.hgtech.expensehistory.ui.custom.dialog.AskDialog
import com.hgtech.expensehistory.ui.manager.adapter.UserCategoryListAdapter
import com.hgtech.expensehistory.ui.viewModel.AddUpdateRootViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseRootModifyFragment<T>(
) : BaseModifyFragment<FragmentUpdateRootBinding>(
    FragmentUpdateRootBinding::inflate,
    true
) {

    val viewModel by viewModel<AddUpdateRootViewModel>()

    val adapter by lazy {
        UserCategoryListAdapter<T> {
            onItemClicked(it)
        }
    }


    abstract fun onItemClicked(it: T)

    abstract fun FragmentUpdateRootBinding.initLayout()
    override fun onExit() = Unit

    override fun initLayout() {
        with(binding) {
            loadData()
            topBar.setup()
            this.initLayout()
        }
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
                    deleteItem(true)
            }
    }

    private var isListSelected = false
    fun LayoutChildListBinding.setup(name: String) {
        list.adapter = adapter
        isListSelected = true
        title.text = name
        setExpanded()
        topTitle.setOnClickListener {
            setExpanded()
        }
    }

    private fun LayoutChildListBinding.setExpanded() {
        isListSelected = !isListSelected
        list.isVisible = isListSelected
        arrow.setImageResource(
            if (isListSelected)
                R.drawable.baseline_arrow_drop_up_24
            else
                R.drawable.baseline_arrow_drop_down_24
        )
    }

    fun View.loadCategories(
        category: AppCompatTextView,
        categoryId: Int = 0
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllCategories().collectLatest { list ->
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    setupPopupCategories(this@loadCategories, category, null, list, categoryId)
                }
            }
        }
    }

    fun View.loadUsers(
        user: AppCompatTextView,
        userId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllUsers().collectLatest { users ->
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    setupUserPopupMenu(this@loadUsers, user, users, userId)
                }
            }
        }
    }

    fun updateRoot(userId: Int, categoryId: Int) {
        updateMenuSelection(userId, categoryId)
    }
}
