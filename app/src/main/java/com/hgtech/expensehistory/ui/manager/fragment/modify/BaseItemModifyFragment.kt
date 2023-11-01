package com.hgtech.expensehistory.ui.manager.fragment.modify

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.hgtech.expensehistory.database.model.ItemTable
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionEditBinding
import com.hgtech.expensehistory.ui.custom.dialog.AskDialog
import com.hgtech.expensehistory.ui.manager.getDate
import com.hgtech.expensehistory.ui.manager.getText
import com.hgtech.expensehistory.ui.viewModel.AddUpdateItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseItemModifyFragment<B : ViewBinding>
    (
    bindingFactory: (LayoutInflater) -> B,
    val isCustomBackPress: Boolean = false
) : BaseModifyFragment<B>(bindingFactory, isCustomBackPress) {

    private var job: Job? = null
    val viewModel: AddUpdateItemViewModel by viewModel<AddUpdateItemViewModel>()


    override fun askDelete() {
        AskDialog.delete(requireContext()) {
            if (it)
                deleteItem(false)
        }
    }

    fun View.loadCategories(
        category: AppCompatTextView,
        description: LayoutItemTitleDescriptionEditBinding
    ) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getAllCategories().collectLatest { categories ->
                    setupPopupCategories(this@loadCategories, category, description, categories)
                }
            }
        }
    }

    fun checkValidData(
        id: Int = 0,
        date: AppCompatTextView,
        description: LayoutItemTitleDescriptionEditBinding,
        total: AppCompatEditText,
        isUpdate: Boolean = false
    ) {
        if (date.text.toString().isEmpty())
            Toast.makeText(requireContext(), "Add Invoice Date", Toast.LENGTH_LONG).show()
        else if (getCategorySelected() == 0)
            Toast.makeText(requireContext(), "Add Invoice Category", Toast.LENGTH_LONG).show()
        else if (description.root.isVisible && description.getText().isEmpty()) {
            description.value.error = "Add Detail"
            Toast.makeText(requireContext(), "Add Invoice Detail", Toast.LENGTH_LONG).show()
        } else if (total.text.toString().isEmpty()) {
            total.error = "Add Total"
            Toast.makeText(requireContext(), "Add Invoice Total", Toast.LENGTH_LONG).show()
        } else
            CoroutineScope(Dispatchers.IO).launch {
                if (isUpdate)
                    updateItem(id, date, description.value, total)
                else
                    insertItem(date, description.value, total)
            }
    }


    private suspend fun insertItem(
        date: AppCompatTextView,
        description: AppCompatEditText,
        total: AppCompatEditText
    ) {
        viewModel.insertItem(generateItem(getCategorySelected(), date, description, total)).also {
            saveResponse()
        }
    }

    private suspend fun updateItem(
        id: Int,
        date: AppCompatTextView,
        description: AppCompatEditText,
        total: AppCompatEditText
    ) {
        viewModel.updateItem(generateItem(getCategorySelected(), date, description, total, id))
            .also {
            saveResponse()
        }
    }


    private fun generateItem(
        category: Int,
        date: AppCompatTextView,
        description: AppCompatEditText,
        total: AppCompatEditText,
        id: Int = 0,
    ): ItemTable {
        return ItemTable(
            id,
            category,
            1,
            getDate(date.text.toString()),
            description.text.toString(),
            total.text.toString().toDouble()
        )
    }

    override fun checkBeforeSaving() = Unit

    override fun deleteItem(isRoot: Boolean) = Unit

    override fun updateLayout(isEnabled: Boolean, shouldNotLoadData: Boolean) = Unit

    override fun loadData() = Unit
}