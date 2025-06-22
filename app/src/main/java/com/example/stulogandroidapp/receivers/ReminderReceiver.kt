package com.example.stulogandroidapp.receivers

import android.app.*
import android.content.*
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.stulogandroidapp.R
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("taskName") ?: "Task Reminder"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, "stulog_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("StuLog Reminder")
            .setContentText("You have a task due soon: $taskName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("stulog_channel", "StuLog Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random().nextInt(), builder.build())
    }
}