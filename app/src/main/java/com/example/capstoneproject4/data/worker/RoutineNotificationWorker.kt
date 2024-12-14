package com.example.capstoneproject4.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.capstoneproject4.R

class RoutineNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        createNotificationChannel()
        val productName = inputData.getString("PRODUCT_NAME") ?: "Unknown Product"
        val routineTime = inputData.getString("ROUTINE_TIME") ?: "Unknown Time"

        Log.d("RoutineNotificationWorker", "Starting to create notification...")
        Log.d("RoutineNotificationWorker", "Product Name: $productName, Routine Time: $routineTime")

        try {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(applicationContext, "ROUTINE_CHANNEL")
                .setSmallIcon(R.drawable.group_5)
                .setContentTitle("Routine Reminder")
                .setContentText("It's time to use $productName at $routineTime.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(productName.hashCode(), notification)
            Log.d("RoutineNotificationWorker", "Notification created successfully.")
        } catch (e: Exception) {
            Log.e("RoutineNotificationWorker", "Error creating notification: ${e.message}")
            return Result.failure()
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "ROUTINE_CHANNEL"
            val channelName = "Routine Notifications"
            val channelDescription = "Notifications for routine reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("RoutineNotificationWorker", "Notification channel created: $channelId")
        }
    }

}
