package com.hgtech.expensehistory.ui.adapter.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.hgtech.expensehistory.database.relation.ItemCategory
import com.hgtech.expensehistory.databinding.ItemsListItemBinding
import com.hgtech.expensehistory.ui.manager.format
import com.hgtech.expensehistory.ui.manager.getItemDate

class ItemListAdapter(
    private val values: List<ItemCategory>,
    private val onItemClicked: (item: ItemCategory) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        bindValue(holder, item)
        holder.itemView.setOnClickListener {
            onItemClicked.invoke(item)
        }

    }

    private fun bindValue(holder: ViewHolder, item: ItemCategory) {
        holder.date.text = item.itemTable?.date?.getItemDate()
        holder.category.text = item.categoryTable?.title
        holder.total.text = item.itemTable?.total?.format()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val date: AppCompatTextView = binding.date
        val category: AppCompatTextView = binding.category
        val total: AppCompatTextView = binding.total
    }
}