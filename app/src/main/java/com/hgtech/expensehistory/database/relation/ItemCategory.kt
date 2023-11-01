package com.hgtech.expensehistory.database.relation

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.ItemTable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemCategory(

    @Embedded
    var itemTable: ItemTable? = null,

    @Relation(
        parentColumn = "category",
        entityColumn = "id"
    )
    var categoryTable: CategoryTable? = null,
) : Parcelable
