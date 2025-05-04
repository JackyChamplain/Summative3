package com.example.summative3.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TaskNotificationService(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "task_reminders"
        private const val CHANNEL_NAME = "Task Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for upcoming or due tasks"
        private const val NOTIFICATION_ID = 1 // You'll likely need to generate unique IDs
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationService", "Notification channel created") // Added log
        }
    }

    fun showTaskNotification(taskId: Long, taskTitle: String, taskDescription: String) {
        Log.d("NotificationService", "showTaskNotification called with ID: $taskId, title: $taskTitle") // Added log

        val intent = Intent(context, Class.forName("com.example.summative3.MainActivity")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", taskId)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                Log.d("NotificationService", "Attempting to notify with ID: $NOTIFICATION_ID") // Added log
                notify(NOTIFICATION_ID, builder.build())
                Log.d("NotificationService", "Notification sent successfully") // Added log
            } catch (e: SecurityException) {
                Log.e("NotificationService", "SecurityException while sending notification: ${e.message}")
            }
        }
    }
}