package com.hgtech.expensehistory.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class UserTable(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val rootId:Int,
    val name: String,
    val isActive: Boolean = true
) : Parcelable
