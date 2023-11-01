package com.hgtech.expensehistory.database.backup.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.hgtech.expensehistory.database.repository.BackupRestoreGSON
import com.hgtech.expensehistory.database.repository.BackupRestoreRepository
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RestoreWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val repository: BackupRestoreRepository by inject()

    companion object {
        const val TAG = "RestoreWorker"
    }

    override suspend fun doWork(): Result {
        delay(3000)
        return try {
            inputData.getString(BackupManager.URI_BACKUP)!!.also {
                repository.restore(Gson().fromJson(it, BackupRestoreGSON::class.java))
            }
            BackupManager.showNotification(
                context,
                "Restore Backup Finish",
                "Finish Restoring Backup",
                isRunning = false,
                isNotDismissible = false
            )
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error Getting Projects -> ${throwable.message}")
            BackupManager.showNotification(
                context,
                "Restore Interrupted",
                "Couldn't Restore backup data",
                false
            )
            Result.failure()
        }
    }

}