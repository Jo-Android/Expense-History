package com.hgtech.expensehistory.ui.fragment

import android.text.InputType
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.databinding.FragmentItemEditBinding
import com.hgtech.expensehistory.ui.manager.fragment.modify.BaseItemModifyFragment
import com.hgtech.expensehistory.ui.manager.getItemDate
import com.hgtech.expensehistory.ui.manager.setupDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItemEditFragment :
    BaseItemModifyFragment<FragmentItemEditBinding>(FragmentItemEditBinding::inflate, true) {

    private val args by navArgs<ItemEditFragmentArgs>()
    override fun onExit() = Unit

    override fun checkBeforeSaving() {
        checkValidData(
            args.invoice.itemTable?.id ?: 0,
            binding.dateLayout.value,
            binding.description,
            binding.priceLayout.value,
            true
        )
    }

    override fun observe() {
    }

    override fun initLayout() {
        super.initLayout()
        with(binding) {
            this.configureHead()
            topBar.setup()
            categoryLayout.root.loadCategories(
                binding.categoryLayout.value,
                binding.description
            )
            categoryLayout.root.setOnClickListener {
                showCategoryMenu()
            }

            dateLayout.root.setOnClickListener {
                if (isEdit)
                    activity?.let { setupDatePicker(it, binding.dateLayout.value) }
            }
        }
    }


    override fun loadData() {
        with(binding) {
            dateLayout.value.text = args.invoice.itemTable?.date.getItemDate()
            categoryLayout.value.text = args.invoice.categoryTable?.title.toString()
            args.invoice.itemTable?.description.also {
                description.root.isVisible = ((it ?: "").isNotEmpty())
                description.value.setText(it)
            }
            updateMenuSelection(0, args.invoice.itemTable?.category ?: 0)
            priceLayout.value.setText(
                StringBuilder(args.invoice.itemTable?.total.toString())
                /*.append(" ")
                .append(args.invoice.itemTable?.currency)*/
            )
        }

    }

    override fun deleteItem(isRoot: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            args.invoice.itemTable.also { item ->
                if (item == null)
                    getErrorDeleting()
                else
                    viewModel.delete(item).also { response ->
                        if (response <= 0)
                            getErrorDeleting()
                        else
                            withContext(Dispatchers.Main) {
                                onBackPressed()
                            }
                    }
            }
        }
    }


    private fun FragmentItemEditBinding.configureHead() {
        description.root.isVisible = args.invoice.categoryTable?.shouldHaveDetail ?: false
        dateLayout.title.text = getString(R.string.date_title)
        categoryLayout.title.text = getString(R.string.category_title)
        description.title.text = getString(R.string.description)
        description.value.hint = getString(R.string.enter_description)
        priceLayout.title.text = getString(R.string.total_price)
        priceLayout.value.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    override fun updateLayout(isEnabled: Boolean, shouldNotLoadData: Boolean) {
        with(binding) {
            description.value.isEditEnable(isEnabled)
            priceLayout.value.isEditEnable(isEnabled)
            topBar.updateLayout(isEnabled)
        }
    }
}

