package com.codetaker.ammusic

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService

class Notification : AppCompatActivity() {
    companion object{

    }

    @SuppressLint("RemoteViewLayout")
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        createNotification(this)
    }


    fun createNotification(context: Context) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the Intent to open when the user taps on the notification
        val intent = Intent(context, PlaySong::class.java).apply {
            putExtra("notification", "notification")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                0
            }
        )

        val notificationLayout = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_large)


        val notification = NotificationCompat.Builder(context, "")
            .setSmallIcon(R.drawable.logo)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
            )
            .setContentTitle("Don't miss this product")
            .setAutoCancel(true)
            .setOngoing(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
//            .setSubText("hello this is testing notification")
//            .setProgress(0,0,true)
            .setContentIntent(pendingIntent)
        notificationManager.notify(1, notification.build())
    }

}