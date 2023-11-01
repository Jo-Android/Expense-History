package com.hgtech.expensehistory.ui.fragment.settings.category

import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.databinding.FragmentNewCategoryBinding
import com.hgtech.expensehistory.ui.manager.PopupMenuHandler
import com.hgtech.expensehistory.ui.manager.fragment.BaseMarginBottomSheet
import com.hgtech.expensehistory.ui.manager.getText
import com.hgtech.expensehistory.ui.manager.hideKeyboard
import com.hgtech.expensehistory.ui.viewModel.AddUpdateRootViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewCategoryFragment :
    BaseMarginBottomSheet<FragmentNewCategoryBinding>(FragmentNewCategoryBinding::inflate) {

    private val viewModel by viewModel<AddUpdateRootViewModel>()
    private val popupMenu = PopupMenuHandler()
    override fun setupLayout() {
        binding.apply {
            topBar.title.text = getString(R.string.new_category)
            name.apply {
                title.text = getString(R.string.name)
                value.apply {
                    setHint(getString(R.string.category_name))
                    isFocusableInTouchMode = true
                    isFocusable = true
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    inputType = InputType.TYPE_CLASS_TEXT
                }
            }
            root.title.text = getString(R.string.root_category)
            user.title.text = getString(R.string.root_user)
        }
    }

    override fun setListeners() {
        binding.apply {
            root.root.setOnClickListener {
                hideKeyboard(requireActivity())
                popupMenu.showCategory()
            }
            user.root.setOnClickListener {
                hideKeyboard(requireActivity())
                popupMenu.showUser()
            }
            topBar.cancel.setOnClickListener {
                findNavController().popBackStack()
            }

            topBar.save.setOnClickListener {
                checkBeforeSaving()
            }
        }
    }

    private fun FragmentNewCategoryBinding.checkBeforeSaving() {
        if (name.getText().isEmpty())
            name.value.error = getString(R.string.enter_name)
        else if (user.getText().isEmpty())
            Toast.makeText(requireContext(), getString(R.string.add_user), Toast.LENGTH_SHORT)
                .show()
        else
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.insertCategory(
                    CategoryTable(
                        0,
                        popupMenu.getCategory(),
                        popupMenu.getUser(),
                        name.getText(),
                        isDescription.isChecked
                    )
                ).also {
                    withContext(Dispatchers.Main) {
                        if (it > 0)
                            findNavController().popBackStack()
                        else
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_category_insert),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
    }

    override fun observe() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllCategories().collectLatest {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    popupMenu.setupPopupCategories(
                        requireActivity(),
                        binding.root.root,
                        binding.root.value,
                        null,
                        it,
                        0
                    )
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllUsers().collectLatest {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    popupMenu.setupUserPopupMenu(
                        requireActivity(),
                        binding.user.root,
                        binding.user.value,
                        it,
                        0
                    )
                }
            }
        }
    }

}