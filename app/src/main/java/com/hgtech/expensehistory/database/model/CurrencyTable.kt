package com.hgtech.expensehistory.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyTable(
    @PrimaryKey
    val id:Int,
    val title:String,
    val symbol:String
)
