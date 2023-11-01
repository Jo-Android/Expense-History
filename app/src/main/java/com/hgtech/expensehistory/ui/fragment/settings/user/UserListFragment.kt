package com.hgtech.expensehistory.ui.fragment.settings.user

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.ui.manager.fragment.BaseRootListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserListFragment : BaseRootListFragment<UserTable>() {
    override fun onItemSelected(it: UserTable) {
        findNavController().navigate(
            UserListFragmentDirections.actionUserListFragmentToUpdateUserFragment(
                it
            )
        )
    }

    override fun observe() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getUsers().collectLatest {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    withContext(Dispatchers.Main) { adapter.addList(it) }
                }
            }
        }
    }

    override fun onAddClicked() {
        findNavController().navigate(UserListFragmentDirections.actionUserListFragmentToNewUserFragment())
    }

}