package com.hgtech.expensehistory.ui.fragment.settings

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.hgtech.expensehistory.databinding.FragmentSettingsBinding
import com.hgtech.expensehistory.ui.manager.fragment.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    override fun observe() = Unit

    override fun initLayout() {
        with(binding) {
            pdf.isVisible = false
            topBar.backButton.setOnClickListener {
                handleOnBackPressed()
            }
            userMenu.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUserListFragment())
            }
            categoryMenu.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToCategoryListFragment())
            }
            backupRestore.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToBackupRestoreFragment())
            }
        }
    }

    override fun onBackPressed() = Unit

}