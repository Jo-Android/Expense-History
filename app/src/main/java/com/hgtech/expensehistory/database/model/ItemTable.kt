package com.hgtech.expensehistory.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity
data class ItemTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val category: Int,
    val currency: Int,
    val date: Date?,
    val description: String,
    val total: Double,
) : Parcelable
