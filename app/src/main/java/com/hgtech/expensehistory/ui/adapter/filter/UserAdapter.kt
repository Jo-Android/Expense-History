package com.hgtech.expensehistory.ui.adapter.filter

import com.hgtech.expensehistory.database.repository.Users
import com.hgtech.expensehistory.ui.manager.adapter.SortListAdapter

class UserAdapter(
    val addRemove: () -> Unit
) : SortListAdapter<Users, Int>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[holder.bindingAdapterPosition].also { item ->
            holder.title.text = item.user.name
            holder.checkBox.setChecking(item.isChecked, item.user.id)
            holder.itemView.setOnClickListener {
                item.isChecked = !item.isChecked
                holder.checkBox.setChecking(item.isChecked, item.user.id)
                addRemove.invoke()
            }
        }
    }

    fun clearSelection() {
        list.forEach { it.isChecked = false }
        resetChecking()
    }
}