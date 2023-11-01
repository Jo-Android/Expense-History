package com.hgtech.expensehistory.ui.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable
import com.hgtech.expensehistory.databinding.ItemsListUserCaetoryBinding

class UserCategoryListAdapter<T>(
    private val onItemClicked: (item: T) -> Unit
) : RecyclerView.Adapter<UserCategoryListAdapter<T>.ViewHolder>() {

    private var values: List<T> = listOf()

    fun addList(list: List<T>) {
        values = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemsListUserCaetoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        values[holder.absoluteAdapterPosition].also { item ->
            bindValues(item, holder)
            holder.itemView.setOnClickListener {
                onItemClicked.invoke(item)
            }
        }
    }

    private fun bindValues(item: T, holder: ViewHolder) {
        when (item) {
            is CategoryTable -> {
                holder.title.text = item.title
            }

            is UserTable -> {
                holder.title.text = item.name
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemsListUserCaetoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
    }

}