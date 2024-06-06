package com.codetaker.ammusic.extensions

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.codetaker.ammusic.utils.SharePrefManager

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    host: Int,
    addStack: Boolean,
    data: Bundle? = null,
): FragmentTransaction {
    val transaction = supportFragmentManager.beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(host, fragment.apply {
            data?.let { arguments = it }
        })

    if (addStack && transaction.isAddToBackStackAllowed) {
        transaction.addToBackStack(fragment::class.java.simpleName)
    }

    transaction.commit()
    return transaction
}

fun AppCompatActivity.replaceFragmentIfNeeded(fragment: Fragment, containerId: Int) {
    val currentFragment = supportFragmentManager.findFragmentById(containerId)
    if (currentFragment?.javaClass != fragment.javaClass) {
        replaceFragment(fragment, containerId, true)
    }
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    containerId: Int,
    addToBackStack: Boolean,
) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(containerId, fragment)
    if (addToBackStack) {
        transaction.addToBackStack(fragment.javaClass.name)
    }
    transaction.commit()
}

fun AppCompatActivity.unauthorized() {
    SharePrefManager.getPrefInstance(this).clearData()
//    val intent = Intent(this, LoginActivity::class.java)
//    startActivity(intent)
//    overridePendingTransition(0, 0)
//    finishAffinity()
}