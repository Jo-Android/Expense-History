package com.hgtech.expensehistory.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hgtech.expensehistory.database.model.ItemTable
import com.hgtech.expensehistory.database.relation.ItemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface ItemDao {
    @Transaction
    @Query("Select * from ItemTable order by date DESC")
    fun getAllItemsWithInformation(): Flow<List<ItemCategory>?>

    fun getAllItemsDistinct(): Flow<List<ItemCategory>?> {
        return getAllItemsWithInformation().distinctUntilChanged()
    }

    @Transaction
    @Query("Select * from ItemTable")
    suspend fun getAllItems(): List<ItemCategory>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(itemTable: ItemTable): Long

    @Update
    suspend fun updateItem(itemTable: ItemTable): Int

    @Delete
    suspend fun delete(itemTable: ItemTable): Int
}
