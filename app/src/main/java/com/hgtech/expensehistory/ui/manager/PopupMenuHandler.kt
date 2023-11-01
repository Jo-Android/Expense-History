package com.hgtech.expensehistory.ui.manager

import android.view.Menu
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionEditBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PopupMenuHandler {

    private var categoryMenu: PopupMenu? = null
    private var categorySelected: Int = 0
    private var userSelected: Int = 0
    private var userMenu: PopupMenu? = null

    suspend fun setupUserPopupMenu(
        activity: FragmentActivity,
        menu: View,
        name: AppCompatTextView,
        users: List<UserTable>,
        rootId: Int = 0
    ) {
        userMenu = setupPopupMenu(menu, name, generateUserList(users, rootId), activity) { id ->
            userSelected = id
        }
    }

    private fun generateUserList(users: List<UserTable>, rootId: Int): List<PopUpMenu> {
        users.map { PopUpMenu(it.id, it.name) }.also { list ->
            return getMenuList(list, rootId)
        }
    }

    suspend fun setupPopupCategories(
        activity: FragmentActivity,
        menu: View,
        name: AppCompatTextView,
        description: LayoutItemTitleDescriptionEditBinding?,
        categories: List<CategoryTable>,
        rootId: Int = 0
    ) {
        categoryMenu =
            setupPopupMenu(menu, name, generateCategoryList(categories, rootId), activity) { id ->
                categorySelected = id
                categories.find { it.id == id }?.let {
                    description?.root?.isVisible = it.shouldHaveDetail
                    if (it.shouldHaveDetail)
                        description?.value?.setText("")
                }
            }
    }

    private fun generateCategoryList(
        categories: List<CategoryTable>,
        rootId: Int
    ): List<PopUpMenu> {
        categories.map { PopUpMenu(it.id, it.title) }.also { list ->
            return getMenuList(list, rootId)
        }
    }

    private fun getMenuList(list: List<PopUpMenu>, rootId: Int): List<PopUpMenu> {
        if (rootId != 0) {
            list.toMutableList().also {
                it.removeIf { menu -> menu.id == rootId }
                it.add(0, PopUpMenu(0, "NONE"))
                return it
            }
        } else
            return list
    }

    private suspend fun setupPopupMenu(
        menu: View,
        name: AppCompatTextView,
        list: List<PopUpMenu>,
        activity: FragmentActivity,
        onItemSelected: (id: Int) -> Unit
    ): PopupMenu {
        withContext(Dispatchers.Main) { hideKeyboard(activity) }
        return list.showPopup(menu, name, activity, onItemSelected)
    }


    private fun List<PopUpMenu>.showPopup(
        menuView: View,
        textSelection: AppCompatTextView,
        activity: FragmentActivity,
        onItemSelected: (id: Int) -> Unit,
    ): PopupMenu {
        val p = PopupMenu(activity, menuView)
        this.forEach {
            p.menu.add(Menu.NONE, it.id, Menu.NONE, it.title)
        }
        p.setOnMenuItemClickListener { menu ->
            if (menu.itemId != 0) {
                textSelection.text = menu.title
                this.find { it.id == menu.itemId }.also { item ->
                    onItemSelected.invoke(item?.id ?: 0)
                }
            } else {
                textSelection.text = ""
                onItemSelected.invoke(0)
            }
            true
        }
        return p
    }

    fun updateMenuSelection(userId: Int, categoryId: Int) {
        userSelected = userId
        categorySelected = categoryId
    }

    fun showCategory() {
        categoryMenu?.show()
    }

    fun showUser() {
        userMenu?.show()
    }

    fun getUser(): Int {
        return userSelected
    }

    fun getCategory(): Int {
        return categorySelected
    }
}

data class PopUpMenu(
    val id: Int,
    val title: String
)
