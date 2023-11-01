package com.hgtech.expensehistory.ui.fragment.settings.user

import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.databinding.FragmentNewUserBinding
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

class NewUserFragment :
    BaseMarginBottomSheet<FragmentNewUserBinding>(FragmentNewUserBinding::inflate) {

    private val viewModel by viewModel<AddUpdateRootViewModel>()
    private val popupMenu = PopupMenuHandler()
    override fun setupLayout() {
        binding.apply {
            topBar.title.text = getString(R.string.new_user)
            name.apply {
                title.text = getString(R.string.name)
                value.apply {
                    setHint(getString(R.string.user_name))
                    isFocusableInTouchMode = true
                    isFocusable = true
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    inputType = InputType.TYPE_CLASS_TEXT
                }
            }
            root.title.text = getString(R.string.root_user)
        }
    }

    override fun setListeners() {
        binding.apply {
            root.root.setOnClickListener {
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

    private fun FragmentNewUserBinding.checkBeforeSaving() {
        if (name.getText().isEmpty())
            name.value.error = getString(R.string.enter_name)
        else
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.insertUser(UserTable(0, popupMenu.getUser(), name.getText())).also {
                    withContext(Dispatchers.Main) {
                        if (it > 0)
                            findNavController().popBackStack()
                        else
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_user_insert),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
    }

    override fun observe() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllUsers().collectLatest {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    popupMenu.setupUserPopupMenu(
                        requireActivity(),
                        binding.root.root,
                        binding.root.value,
                        it,
                        0
                    )
                }
            }
        }
    }

}
