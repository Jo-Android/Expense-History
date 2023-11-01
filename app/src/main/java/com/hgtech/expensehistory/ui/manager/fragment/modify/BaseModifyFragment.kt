package com.hgtech.expensehistory.ui.manager.fragment.modify

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.databinding.LayoutEditTopBinding
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionEditBinding
import com.hgtech.expensehistory.ui.custom.dialog.AskDialog
import com.hgtech.expensehistory.ui.manager.PopupMenuHandler
import com.hgtech.expensehistory.ui.manager.fragment.BaseFragment
import com.hgtech.expensehistory.ui.manager.hideKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseModifyFragment<B : ViewBinding>(
    bindingFactory: (LayoutInflater) -> B,
    customBackPress: Boolean = false,
) : BaseFragment<B>(bindingFactory, customBackPress) {

    var isEdit: Boolean = false

    abstract fun onExit()

    override fun initLayout() {
        updateLayout(isEnabled = true, shouldNotLoadData = false)
    }

    override fun onBackPressed() {
        if (isEdit)
            showBackPressDialog()
        else {
            isEdit = false
            handelBackPressed()
        }
    }

    private fun showBackPressDialog() {
        AskDialog.showEditDialog(getString(R.string.confirm_update_exit), requireContext()) {
//            handelBackPressed()
            isEdit = false
            updateLayout(isEnabled = true, shouldNotLoadData = false)
        }
    }


    fun LayoutEditTopBinding.setup() {
        backButton.setOnClickListener {
            hideKeyboard(requireActivity())
            if (isEdit) {
                this@BaseModifyFragment.updateLayout(isEnabled = true, shouldNotLoadData = false)
                isEdit = false
            } else
                onBackPressed()
        }

        delete.setOnClickListener {
            askDelete()
        }


        actionButton.setOnClickListener {
            hideKeyboard(requireActivity())
            if (!isEdit) {
                isEdit = true
                this@BaseModifyFragment.updateLayout(isEnabled = false, shouldNotLoadData = false)
            } else
                checkBeforeSaving()
        }
    }

    abstract fun askDelete()

    abstract fun checkBeforeSaving()

    abstract fun deleteItem(isRoot: Boolean)

    abstract fun updateLayout(isEnabled: Boolean, shouldNotLoadData: Boolean)

    fun AppCompatEditText.isEditEnable(isAlreadyUpdate: Boolean) {
        isFocusableInTouchMode = !isAlreadyUpdate
        isFocusable = !isAlreadyUpdate
    }

    fun LayoutEditTopBinding.updateLayout(isEnable: Boolean, shouldNotLoadData: Boolean = false) {
        if (!isEnable) {
            actionButton.setImageResource(R.drawable.baseline_check_24)
            backButton.setImageResource(R.drawable.baseline_close_24)
        } else {
            actionButton.setImageResource(R.drawable.baseline_edit_24)
            backButton.setImageResource(R.drawable.baseline_arrow_back_ios_24)
        }
        delete.isVisible = isEnable
        if (isEnable && !shouldNotLoadData)
            loadData()
    }

    abstract fun loadData()

    fun getErrorDeleting() {
        Toast.makeText(
            requireContext(),
            "Couldn't delete Item",
            Toast.LENGTH_LONG
        ).show()
    }

    suspend fun saveResponse(isExit: Boolean = true) {
        isEdit = false
        onExit()
        withContext(Dispatchers.Main) {
            if (isExit)
                handleOnBackPressed()
            else
                updateLayout(isEnabled = true, shouldNotLoadData = true)
        }
    }


    private val popUpMenu = PopupMenuHandler()

    suspend fun setupUserPopupMenu(
        menu: View,
        name: AppCompatTextView,
        list: List<UserTable>,
        id: Int = 0
    ) {
        popUpMenu.setupUserPopupMenu(requireActivity(), menu, name, list, id)
    }

    suspend fun setupPopupCategories(
        menu: View,
        name: AppCompatTextView,
        description: LayoutItemTitleDescriptionEditBinding?,
        list: List<CategoryTable>,
        id: Int = 0
    ) {
        popUpMenu.setupPopupCategories(requireActivity(), menu, name, description, list, id)
    }

    fun updateMenuSelection(userId: Int, categoryId: Int) {
        popUpMenu.updateMenuSelection(userId, categoryId)
    }

    fun getUserSelected(): Int {
        return popUpMenu.getUser()
    }

    fun getCategorySelected(): Int {
        return popUpMenu.getCategory()
    }

    fun showCategoryMenu() {
        if (isEdit)
            popUpMenu.showCategory()
    }

    fun setupUserMenu() {
        if (isEdit)
            popUpMenu.showUser()

    }

    // Popup Menu Setups
}