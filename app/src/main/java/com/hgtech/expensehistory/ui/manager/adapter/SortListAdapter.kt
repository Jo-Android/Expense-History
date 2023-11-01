package com.hgtech.expensehistory.ui.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.databinding.ItemsSortListBinding

abstract class SortListAdapter<T, B> : RecyclerView.Adapter<SortListAdapter.ViewHolder>() {

    var list = ArrayList<T>()
    private var selectedList = ArrayList<B>()
    class ViewHolder(binding: ItemsSortListBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val checkBox = binding.checkbox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsSortListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(list: List<T>) {
        this.list = ArrayList()
        this.list.addAll(list)
        notifyItemRangeChanged(0, itemCount)
    }

    fun AppCompatImageView.setChecking(isChecked: Boolean, item: B) {
        setImageResource(
            if (isChecked) {
                selectedList.add(item)
                R.drawable.item_checked
            } else {
                selectedList.removeIf { it == item }
                R.drawable.item_not_checked
            }
        )
    }

    fun hasSelection(): Boolean {
        return selectedList.isNotEmpty()
    }

    fun resetChecking() {
        selectedList = ArrayList()
        notifyItemRangeChanged(0, itemCount)
    }

    fun getSelected(): List<B> {
        return selectedList
    }
}