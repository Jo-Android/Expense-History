package com.hgtech.expensehistory.ui.fragment.settings.user

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.databinding.FragmentUpdateRootBinding
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionEditBinding
import com.hgtech.expensehistory.ui.manager.fragment.modify.BaseRootModifyFragment
import com.hgtech.expensehistory.ui.manager.getText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateUserFragment :
    BaseRootModifyFragment<UserTable>() {

    private val args by navArgs<UpdateUserFragmentArgs>()
    override fun onItemClicked(it: UserTable) {
        findNavController().navigate(UpdateUserFragmentDirections.actionUpdateUserFragmentSelf(it))
    }

    override fun FragmentUpdateRootBinding.initLayout() {
        rootCategory.root.isVisible = false
        isDescription.isVisible = false
        name.title.text = getString(R.string.name)
        name.value.setHint(getString(R.string.user_name))
        rootUser.apply {
            root.loadUsers(value, args.user.id)
            root.setOnClickListener { setupUserMenu() }
            title.text = getString(R.string.root_user)
        }
        rootChild.setup(getString(R.string.sub_users))
    }

    override fun checkBeforeSaving() {
        binding.apply {
            checkUserValidData(args.user.id, name)
        }
    }

    private fun checkUserValidData(
        id: Int,
        name: LayoutItemTitleDescriptionEditBinding,
    ) {
        if (name.getText().isEmpty()) {
            name.value.error = "Add Name"
            Toast.makeText(requireContext(), getString(R.string.user_name), Toast.LENGTH_LONG)
                .show()
        } else
            CoroutineScope(Dispatchers.IO).launch {
                updateItem(generateItem(id, name))
            }
    }

    private fun generateItem(
        id: Int = 0,
        name: LayoutItemTitleDescriptionEditBinding,
    ): UserTable {
        return UserTable(id, getUserSelected(), name.getText())
    }

    private suspend fun updateItem(userTable: UserTable) {
        viewModel.updateUser(userTable).also { saveResponse(false) }
    }

    override fun deleteItem(isRoot: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteUser(args.user, isRoot).also { response ->
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
            topBar.updateLayout(isEnabled, shouldNotLoadData)
            name.value.isEditEnable(isEnabled)
            rootUser.root.isVisible =
                !isEnabled || (args.user.rootId != 0 && !shouldNotLoadData) || rootUser.getText()
                    .isNotEmpty()
            rootChild.root.isVisible = adapter.itemCount > 0 && isEnabled
        }
    }

    override fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getUserName(args.user.rootId).also {
                withContext(Dispatchers.Main) { binding.rootUser.value.text = it }
            }
        }
        args.user.also {
            binding.name.value.setText(it.name)
            updateRoot(it.rootId, 0)
        }
    }

    override fun observe() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllUsersDistinct(args.user.id).collectLatest {
                var isChanged = true
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