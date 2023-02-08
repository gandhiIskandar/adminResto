package com.daharansemar.adminresto

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build


class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            val channel = NotificationChannel(
                FirebaseService.CHANNEL_ID,
                "Pesanan", NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Digunakan untuk menerima pesanan"

            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()


            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),att)
            channel.enableVibration(true)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
            notificationManager.createNotificationChannel(channel)


        }
    }
}