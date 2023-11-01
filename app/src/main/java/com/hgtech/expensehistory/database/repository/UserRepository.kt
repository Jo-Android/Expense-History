package com.hgtech.expensehistory.database.repository

import android.util.Log
import com.hgtech.expensehistory.database.dao.UserDao
import com.hgtech.expensehistory.database.model.UserTable
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun getUsername(userId: Int): String {
        return userDao.getUsername(userId)
    }

    suspend fun getAllUsersDistinct(): Flow<List<UserTable>> {
        return userDao.getAllUsersDistinct()
    }

    suspend fun update(userTable: UserTable): Int {
        return userDao.update(userTable)
    }

    suspend fun delete(user: UserTable, isRoot: Boolean): Int {
        Log.d("TAG", "User $user")
        if (!isRoot)
            return userDao.deactivate(user.id)
        else {
            userDao.changeRoot(user.id).also {
                return if (it > 0)
                    userDao.deactivate(user.id)
                else
                    0
            }
        }
    }

    suspend fun getAllUsers(id: Int): Flow<List<UserTable>> {
        return userDao.getAllUsersDistinct(id)
    }

    suspend fun insert(userTable: UserTable): Long {
        return userDao.insert(userTable)
    }

}
