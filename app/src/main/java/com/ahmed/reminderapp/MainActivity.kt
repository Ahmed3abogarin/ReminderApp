package com.ahmed.reminderapp

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.ahmed.reminderapp.ui.theme.ReminderAppTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // الاشعارات
        createNotificationChannel(this)

        // اذن المنبه
        requestNotificationPermission()




        enableEdgeToEdge()
        setContent {
            ReminderAppTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val context = LocalContext.current
                    var taskDate by remember { mutableStateOf(LocalDate.now()) }
                    var taskTime by remember { mutableStateOf(LocalTime.now()) }

                    // ضبط المنبه
                    TaskDateTimePicker(onTimeChanged = { taskTime = it },
                        onDateChanged = { taskDate = it })

                    Button(onClick = {
                        val taskDateTime = LocalDateTime.of(taskDate, taskTime)
                        val timeInMillis =
                            taskDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        schedule(timeInMillis, "تجربة")
                        Toast.makeText(
                            context,
                            "تم ضبط المنبه على الساعه " + "${taskTime.hour}:${taskTime.minute}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Text(text = "ضبط المنبه")
                    }
                }
            }
        }
    }

    private fun schedule(triggerAtMillis: Long, notificationText: String) {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("title", notificationText)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            triggerAtMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Check permissions for exact alarm scheduling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val cintent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                this.startActivity(cintent)
                return
            }
        }

        // Set the alarm to trigger at the specified time
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )


    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }

}

fun createNotificationChannel(context: Context) {
    val name = "Reminders"
    val descriptionText = "Channel for task reminders"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel("reminder_channel", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

}

