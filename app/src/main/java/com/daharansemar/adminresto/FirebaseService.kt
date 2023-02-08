package com.daharansemar.adminresto

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService(){

private lateinit var notification: Notification

    companion object {
        const val CHANNEL_ID = "notifikasi"
    }

   private fun showNotification(){

       MainActivity.keselAmat = true

        val activityIntent = Intent(this, MainActivity::class.java)

       activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)


        val activityPendingIntent = PendingIntent.getActivity(
            this,
            1,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_ONE_SHOT else 0

        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Ada Pesanan Baru!")
                .setContentText("ayo segera kerjakan")
                .setContentIntent(activityPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .build()

        } else {

            notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Ada Pesanan Baru!")
                .setContentText("ayo segera kerjakan")
                .setContentIntent(activityPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .build()

        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        notificationManager.notify(1,notification)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification()




    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
}