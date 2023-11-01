package com.hgtech.expensehistory.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hgtech.expensehistory.database.model.UserTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface UserDao {
    @Query("Select * from UserTable where isActive=1")
    fun getAllActiveUsers(): Flow<List<UserTable>>

    suspend fun getAllUsersDistinct(): Flow<List<UserTable>> {
        return getAllActiveUsers().distinctUntilChanged()
    }

    @Query("Select * from UserTable ")
    suspend fun getAllUsers(): List<UserTable>

    @Transaction
    @Query("Select * from UserTable where rootId=:id and isActive=1")
    fun getAllUsers(id: Int): Flow<List<UserTable>>

    suspend fun getAllUsersDistinct(id: Int): Flow<List<UserTable>> {
        return getAllUsers(id).distinctUntilChanged()
    }

    @Query("Select name from usertable where id=:userId")
    suspend fun getUsername(userId: Int): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userTable: UserTable): Long

    @Update
    suspend fun update(userTable: UserTable): Int

    @Query("update UserTable set rootId=0 where rootId=:id")
    suspend fun changeRoot(id: Int): Int

    @Query("update UserTable set isActive=0 where id=:id")
    suspend fun deactivate(id: Int): Int
}