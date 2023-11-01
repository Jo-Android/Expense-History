package com.hgtech.expensehistory.database.backup.workmanager

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.LocusIdCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.backup.workmanager.BackupWorker.Companion.CATEGORIES
import com.hgtech.expensehistory.database.backup.workmanager.BackupWorker.Companion.FROM
import com.hgtech.expensehistory.database.backup.workmanager.BackupWorker.Companion.TO
import com.hgtech.expensehistory.database.backup.workmanager.BackupWorker.Companion.USERS
import com.hgtech.expensehistory.database.repository.FilterItems

object BackupManager {
    const val URI_BACKUP = "Backup Path"

    fun generateBackup(
        application: Application,
        context: Context,
        backupPath: Uri,
        intent: FilterItems?
    ): LiveData<WorkInfo> {
        showNotification(
            context,
            "Back Up Started",
            "Backing up your data",
        )
        val work = OneTimeWorkRequestBuilder<BackupWorker>()
            .setInputData(
                Data.Builder().putString(URI_BACKUP, backupPath.toString())
                    .putIntArray(CATEGORIES, (intent?.categories ?: listOf()).toIntArray())
                    .putIntArray(USERS, (intent?.user ?: listOf()).toIntArray())
                    .putLong(FROM, intent?.from ?: 0L)
                    .putLong(TO, intent?.to ?: 0L)
                    .build()
            ).build()
        val it = WorkManager.getInstance(application)
        it.enqueueUniqueWork(
            "BACKUP",
            ExistingWorkPolicy.REPLACE, work
        )

        return it.getWorkInfoByIdLiveData(work.id)
    }


    fun restore(application: Application, context: Context, backup: String): LiveData<WorkInfo> {
        showNotification(
            context,
            "Restore Backup Started",
            "Restoring Backup file",
        )
        val work = OneTimeWorkRequestBuilder<RestoreWorker>()
            .setInputData(
                Data.Builder().putString(URI_BACKUP, backup)
                    .build()
            ).build()
        val it = WorkManager.getInstance(application)
        it.enqueueUniqueWork(
            "RESTORE",
            ExistingWorkPolicy.REPLACE, work
        )

        return it.getWorkInfoByIdLiveData(work.id)
    }

    private const val CHANNEL_NEW_BACKUP_RESTORE = "new_backup_restore"
    private const val NOTIFICATION_ID = 1

    fun showNotification(
        context: Context,
        title: String,
        msg: String,
        isRunning: Boolean = true,
        isNotDismissible: Boolean = true
    ) {

        val builder = NotificationCompat.Builder(context, CHANNEL_NEW_BACKUP_RESTORE)

            .setContentTitle(title)
            .setSmallIcon(R.drawable.baseline_backup_24)
            .setContentText(
                if (isNotDismissible && !isRunning)
                    "A problem occurred \n$msg"
                else
                    msg
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        if (isNotDismissible && !isRunning)
                            "A problem occurred  \n$msg"
                        else
                            msg
                    )
            )
            .setCategory(Notification.CATEGORY_PROGRESS)
            .setShortcutId(NOTIFICATION_ID.toString())
            .setLocusId(LocusIdCompat(NOTIFICATION_ID.toString()))
            .setOngoing(isNotDismissible)
            .setShowWhen(true)
        builder.setProgress(0, 0, isRunning)
        setUpNotificationChannels(context)?.apply {
            cancelAll()
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun setUpNotificationChannels(context: Context): NotificationManager? {

        val notificationManager = (context.getSystemService()) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager?.getNotificationChannel(CHANNEL_NEW_BACKUP_RESTORE) == null) {

                val audioAttributes =
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
                val ringtoneManager =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                NotificationChannel(
                    CHANNEL_NEW_BACKUP_RESTORE,
                    "Backup",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Backup All Data"
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                    setSound(ringtoneManager, audioAttributes)
                    notificationManager?.createNotificationChannel(this)
                }

            }
        }
        return notificationManager
    }
}