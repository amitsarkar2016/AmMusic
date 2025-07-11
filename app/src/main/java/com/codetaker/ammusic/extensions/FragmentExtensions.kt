package com.codetaker.ammusic.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.replaceFragmentIfNeeded(fragment: Fragment, containerId: Int) {
    val currentFragment = parentFragmentManager.findFragmentById(containerId)
    if (currentFragment?.javaClass != fragment.javaClass) {
        replaceFragment(fragment, containerId, true)
    }
}

fun Fragment.replaceFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {
    val transaction = parentFragmentManager.beginTransaction()
    transaction.replace(containerId, fragment)
    if (addToBackStack) {
        transaction.addToBackStack(fragment.javaClass.name)
    }
    transaction.commit()
}

fun Fragment.copyToClipboard(text: String) {
    val clipboardManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requireContext().getSystemService(ClipboardManager::class.java)
    } else {
        @Suppress("DEPRECATION")
        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    val clip = android.content.ClipData.newPlainText("label", text)
    clipboardManager.setPrimaryClip(clip)
}