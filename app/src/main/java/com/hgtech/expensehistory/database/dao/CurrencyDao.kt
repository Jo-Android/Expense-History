package com.hgtech.expensehistory.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hgtech.expensehistory.database.model.CurrencyTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface CurrencyDao {
    @Query("Select * from CurrencyTable")
    fun getAllItems(): Flow<List<CurrencyTable>>

    suspend fun getAllCurrencyDistinct(): Flow<List<CurrencyTable>> {
        return getAllItems().distinctUntilChanged()
    }

    @Query("Select symbol from CurrencyTable where id=1")
    fun getCurrency(): Flow<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(currencyTable: CurrencyTable)

    @Query("Select * from CurrencyTable")
    suspend fun getAllCurrencies(): List<CurrencyTable>
}