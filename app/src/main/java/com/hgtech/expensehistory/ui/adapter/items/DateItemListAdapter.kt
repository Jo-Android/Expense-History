package com.hgtech.expensehistory.ui.adapter.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.hgtech.expensehistory.database.relation.ItemCategory
import com.hgtech.expensehistory.databinding.ItemsListDateItemsBinding
import com.hgtech.expensehistory.ui.manager.getMonthName

class DateItemListAdapter(
    private val onItemClicked: (item: ItemCategory) -> Unit,
    private val onListUpdated: (total: Double) -> Unit
) :
    RecyclerView.Adapter<DateItemListAdapter.ViewHolder>() {


    private var keys: ArrayList<Int> = arrayListOf()
    private var items = ArrayList<List<ItemCategory>>()

    inner class ViewHolder(binding: ItemsListDateItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val month: AppCompatTextView = binding.month
        val items: RecyclerView = binding.items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsListDateItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return keys.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.month.text = getMonthName(keys[holder.bindingAdapterPosition])
        holder.items.adapter = ItemListAdapter(items[holder.bindingAdapterPosition], onItemClicked)
    }

    private var total = 0.0
    fun setItems(map: MutableMap<Int, java.util.ArrayList<ItemCategory>>) {
        keys = ArrayList()
        items = ArrayList()
        total = 0.0
        for ((k, v) in map) {
            keys.add(k)
            items.add(v)
            v.forEach {
                total += it.itemTable?.total ?: 0.0
            }
        }
        onListUpdated.invoke(total)
        notifyDataSetChanged()
    }

}