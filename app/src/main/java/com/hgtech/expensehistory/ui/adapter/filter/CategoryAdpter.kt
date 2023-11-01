package com.hgtech.expensehistory.ui.adapter.filter

import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.repository.Categories
import com.hgtech.expensehistory.ui.manager.adapter.SortListAdapter

class CategoryAdapter(
    private val marginTop: Int,
    val addRemove: () -> Unit
) :
    SortListAdapter<Categories, CategoryTable>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[holder.bindingAdapterPosition].also { item ->

            holder.itemView.isVisible = !item.isHidden
            if (item.isHidden) {
                item.isChecked = false
                holder.itemView.layoutParams = LinearLayout.LayoutParams(1, 1)
            } else {
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, marginTop, 0, 0)
                    holder.itemView.layoutParams = this
                }
            }
            holder.title.text = item.category.title
            holder.checkBox.setChecking(item.isChecked, item.category)
            holder.itemView.setOnClickListener {
                item.isChecked = !item.isChecked
                holder.checkBox.setChecking(item.isChecked, item.category)
                addRemove.invoke()
            }
        }
    }

    fun clearSelection() {
        list.forEach {
            it.isChecked = false
            it.isHidden = false
        }
        resetChecking()
    }

}