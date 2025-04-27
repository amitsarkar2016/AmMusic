package com.codetaker.ammusic.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharePrefManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.packageName, Context.MODE_PRIVATE
    )

    private val _tokenFlow = MutableStateFlow(getToken())
    val tokenFlow: StateFlow<String?> = _tokenFlow


    private val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()

    fun getString(key: String, default: String? = null): String? {
        return sharedPreferences.getString(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
        if (key == TOKEN) {
            _tokenFlow.value = value
        }
    }

    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun clearData() {
        editor.clear().apply()
    }

    fun logout() {
        clearData()
    }

    fun getToken(): String? = getString(TOKEN)
    fun getOnBoardingStatus(): Boolean {
        return sharedPreferences.getBoolean("isOnBoardingDone", false)
    }
    fun setOnBoardingStatus(status: Boolean) {
        sharedPreferences.edit().putBoolean("isOnBoardingDone", status).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: SharePrefManager? = null

        private const val TOKEN = "TOKEN"

        fun getPrefInstance(context: Context): SharePrefManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharePrefManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}