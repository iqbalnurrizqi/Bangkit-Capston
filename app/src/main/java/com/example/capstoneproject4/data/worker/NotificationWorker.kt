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

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val productName = inputData.getString("productName") ?: "Product"
        val routineTime = inputData.getString("routineTime") ?: "Time"

        Log.d("NotificationWorker", "NotificationWorker triggered with data: Product - $productName, Time - $routineTime")

        // Create notification
        try {
            showNotification(productName, routineTime)
            Log.d("NotificationWorker", "Notification sent successfully.")
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error sending notification: ${e.message}")
            return Result.failure()
        }

        return Result.success()
    }

    private fun showNotification(productName: String, routineTime: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "routine_notification_channel"

        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Routine Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled routines"
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationWorker", "Notification channel created.")
        }

        // Build Notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.group_3)
            .setContentTitle("Time for Your Routine!")
            .setContentText("Use $productName at $routineTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show Notification
        notificationManager.notify((0..1000).random(), notification)
    }
}
