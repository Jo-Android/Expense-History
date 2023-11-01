package com.hgtech.expensehistory.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class CategoryTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val rootId: Int = 0,
    val userId: Int,
    var title: String,
    val shouldHaveDetail: Boolean = false,
    val isActive: Boolean = true
) : Parcelable
