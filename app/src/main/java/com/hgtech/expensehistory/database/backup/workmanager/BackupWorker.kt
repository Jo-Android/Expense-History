package com.hgtech.expensehistory.database.backup.workmanager

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.hgtech.expensehistory.database.backup.workmanager.BackupManager.showNotification
import com.hgtech.expensehistory.database.repository.BackupRestoreGSON
import com.hgtech.expensehistory.database.repository.BackupRestoreRepository
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BackupWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val repository: BackupRestoreRepository by inject()

    companion object {
        const val TAG = "ProjectWorker"
        const val CATEGORIES = "CATEGORIES"
        const val USERS = "USERS"
        const val FROM = "FROM"
        const val TO = "TO"
    }

    override suspend fun doWork(): Result {
        delay(3000)
        return try {
            inputData.generateBackup()
            showNotification(
                context,
                "Backup Finish",
                "Finish Backing up data",
                isRunning = false,
                isNotDismissible = false
            )
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error Getting Projects -> ${throwable.message}")
            showNotification(
                context,
                "Backup Interrupted",
                "Couldn't backup Project data",
                false
            )
            Result.failure()
        }
    }

    private suspend fun Data.generateBackup() {
        repository.backup(
            categories = getIntArray(CATEGORIES)!!,
            users = getIntArray(USERS)!!,
            getLong(FROM, 0L),
            getLong(TO, 0L),
        ) {
            createBackup(getString(BackupManager.URI_BACKUP)!!, it)
        }
    }

    private fun createBackup(uri: String, file: BackupRestoreGSON) {
        Log.d("TAG", "Saving file to ${uri} db version ${file.dbVersion}")
        val outputStream = context.contentResolver.openOutputStream(Uri.parse(uri))
        outputStream!!.write(Gson().toJson(file).toByteArray())
        outputStream.close()
    }
}
