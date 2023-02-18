package com.codetaker.ammusic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.codetaker.ammusic.Counter

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val service = NotificationService(context)
        service.showNotification(++Counter.value)
    }
}