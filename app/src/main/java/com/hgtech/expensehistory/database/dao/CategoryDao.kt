package com.hgtech.expensehistory.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hgtech.expensehistory.database.model.CategoryTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface CategoryDao {
    @Transaction
    @Query("Select * from CategoryTable where isActive=1 order by userId")
    fun getAllActiveCategories(): Flow<List<CategoryTable>>

    suspend fun getAllCategoriesDistinct(): Flow<List<CategoryTable>> {
        return getAllActiveCategories().distinctUntilChanged()
    }

    @Query("Select * from CategoryTable ")
    suspend fun getAllCategories(): List<CategoryTable>


    @Query("Select title from CategoryTable where id=:id")
    suspend fun getCategoryName(id: Int): String

    @Query("Select rootId from CategoryTable where id=:category")
    suspend fun getRootCategoryId(category: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(categoryTable: CategoryTable): Long

    @Update
    suspend fun update(categoryTable: CategoryTable): Int

    @Transaction
    @Query("Select * from CategoryTable where rootId=:id and isActive=1")
    fun getAllCategoriesByCategory(id: Int): Flow<List<CategoryTable>>

    suspend fun getAllCategoriesByCategoryDistinct(id: Int): Flow<List<CategoryTable>> {
        return getAllCategoriesByCategory(id).distinctUntilChanged()
    }

    @Query("update CategoryTable set rootId=0 where rootId=:id")
    suspend fun changeRoot(id: Int): Int

    @Query("update CategoryTable set isActive=0 where id=:id")
    suspend fun deactivate(id: Int): Int

}