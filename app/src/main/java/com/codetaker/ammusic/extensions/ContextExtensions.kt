package com.codetaker.ammusic.extensions

import android.content.Context
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat


fun Context.setStatusBarColor(window: Window, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val systemBars = insets.getInsets(WindowInsets.Type.statusBars() or WindowInsetsCompat.Type.displayCutout())
            view.setBackgroundColor(color)

            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    } else {
        window.statusBarColor = color
    }
}