package com.codetaker.ammusic.core

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class PermissionUtil {
    private val requestLocationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val requestStoragePermission = arrayOf(
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) Manifest.permission.WRITE_EXTERNAL_STORAGE else null,
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) Manifest.permission.READ_EXTERNAL_STORAGE else null,
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) Manifest.permission.READ_MEDIA_IMAGES else null,
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) Manifest.permission.READ_MEDIA_VIDEO else null,
    ).filterNotNull().toTypedArray()

    private val requestBluetoothPermission = arrayOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else null
    ).filterNotNull().toTypedArray()

    private val requestContactPermission =
        arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS
        )

    private val requestCameraPermission = arrayOf(Manifest.permission.CAMERA)

    private val requestNotificationPermission = arrayOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null
    ).filterNotNull().toTypedArray()

    private val requestCallLogPermission = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)

    private val requestCallPermission = arrayOf(Manifest.permission.CALL_PHONE)

    private val requestNearByDevicesPermission = arrayOf(
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) Manifest.permission.NEARBY_WIFI_DEVICES else null
    ).filterNotNull().toTypedArray()

    private val requestSMSPermission = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS
    )

    private val requestMicrophonePermission = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )
    private val requestUpdatePermission = arrayOf(
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) Manifest.permission.UPDATE_PACKAGES_WITHOUT_USER_ACTION else null
    ).filterNotNull().toTypedArray()

    val requestAllPermission =
        requestLocationPermission + requestStoragePermission + requestBluetoothPermission + requestContactPermission + requestCameraPermission + requestNotificationPermission + requestCallLogPermission + requestCallPermission + requestNearByDevicesPermission + requestSMSPermission + requestMicrophonePermission + requestUpdatePermission

    fun getMissingPermissions(
        context: Context,
        requestPermission: Array<String>,
    ): List<String> {
        val missingPermissions = mutableListOf<String>()
        for (permission in requestPermission) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missingPermissions.add(permission)
            }
        }
        return missingPermissions
    }

    fun getGrantedPermissions(
        context: Context,
        requestPermission: Array<String>,
    ): List<String> {
        val grantedPermissions = mutableListOf<String>()
        for (permission in requestPermission) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                grantedPermissions.add(permission)
            }
        }
        return grantedPermissions
    }

    fun getMissingPermissions(activity: Activity): List<String> {
        val missingPermissions = mutableListOf<String>()
        for (permission in requestAllPermission) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missingPermissions.add(permission)
            }
        }
        return missingPermissions
    }

    fun getGrantedPermissions(activity: Activity): List<String> {
        val grantedPermissions = mutableListOf<String>()
        for (permission in requestAllPermission) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                grantedPermissions.add(permission)
            }
        }
        return grantedPermissions
    }

    fun isDeveloperModeEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) != 0
    }

    fun getPermission(type: PermissionType): Array<String> {
        return when (type) {
            PermissionType.CAMERA -> requestCameraPermission
            PermissionType.STORAGE -> requestStoragePermission
            PermissionType.LOCATION -> requestLocationPermission
            PermissionType.BLUETOOTH -> requestBluetoothPermission
            PermissionType.CONTACT -> requestContactPermission
            PermissionType.NOTIFICATION -> requestNotificationPermission
            PermissionType.CALL_LOG -> requestCallLogPermission
            PermissionType.CALL -> requestCallPermission
            PermissionType.NEARBY_DEVICES -> requestNearByDevicesPermission
            PermissionType.SMS -> requestSMSPermission
            PermissionType.MICROPHONE -> requestMicrophonePermission
            PermissionType.UPDATE -> requestUpdatePermission
        }
    }

    fun getPermissions(type: List<PermissionType>): Array<String> {
        val newPermissions = mutableListOf<String>()

        type.forEach {
            val addPermission = when (it) {
                PermissionType.CAMERA -> requestCameraPermission
                PermissionType.STORAGE -> requestStoragePermission
                PermissionType.LOCATION -> requestLocationPermission
                PermissionType.BLUETOOTH -> requestBluetoothPermission
                PermissionType.CONTACT -> requestContactPermission
                PermissionType.NOTIFICATION -> requestNotificationPermission
                PermissionType.CALL_LOG -> requestCallLogPermission
                PermissionType.CALL -> requestCallPermission
                PermissionType.NEARBY_DEVICES -> requestNearByDevicesPermission
                PermissionType.SMS -> requestSMSPermission
                PermissionType.MICROPHONE -> requestMicrophonePermission
                PermissionType.UPDATE -> requestUpdatePermission
            }
            newPermissions.addAll(addPermission)
        }
        return newPermissions.toTypedArray()
    }

    fun getMissingPermissionsType(missingPermissions: List<String>): List<PermissionType> {
        val missingPermissionTypes =
            mutableSetOf<PermissionType>() // Use a Set to automatically remove duplicates
        for (permission in missingPermissions) {
            val permissionType = when (permission) {
                in requestCameraPermission -> PermissionType.CAMERA
                in requestStoragePermission -> PermissionType.STORAGE
                in requestLocationPermission -> PermissionType.LOCATION
                in requestBluetoothPermission -> PermissionType.BLUETOOTH
                in requestContactPermission -> PermissionType.CONTACT
                in requestNotificationPermission -> PermissionType.NOTIFICATION
                in requestCallLogPermission -> PermissionType.CALL_LOG
                in requestCallPermission -> PermissionType.CALL
                in requestNearByDevicesPermission -> PermissionType.NEARBY_DEVICES
                in requestSMSPermission -> PermissionType.SMS
                in requestMicrophonePermission -> PermissionType.MICROPHONE
                in requestUpdatePermission -> PermissionType.UPDATE
                else -> null
            }

            permissionType?.let { missingPermissionTypes.add(it) }
        }

        return missingPermissionTypes.toList()
    }

    fun getGrantedPermissionsType(missingPermissions: List<String>): List<PermissionType> {
        val missingPermissionTypes =
            mutableSetOf<PermissionType>() // Use a Set to automatically remove duplicates
        for (permission in missingPermissions) {
            val permissionType = when (permission) {
                in requestCameraPermission -> PermissionType.CAMERA
                in requestStoragePermission -> PermissionType.STORAGE
                in requestLocationPermission -> PermissionType.LOCATION
                in requestBluetoothPermission -> PermissionType.BLUETOOTH
                in requestContactPermission -> PermissionType.CONTACT
                in requestNotificationPermission -> PermissionType.NOTIFICATION
                in requestCallLogPermission -> PermissionType.CALL_LOG
                in requestCallPermission -> PermissionType.CALL
                in requestNearByDevicesPermission -> PermissionType.NEARBY_DEVICES
                in requestSMSPermission -> PermissionType.SMS
                in requestMicrophonePermission -> PermissionType.MICROPHONE
                in requestUpdatePermission -> PermissionType.UPDATE
                else -> null
            }

            permissionType?.let { missingPermissionTypes.add(it) }
        }

        return missingPermissionTypes.toList()
    }

    fun requestPermission(activity: Activity): Boolean {
        val missingPermissions = getMissingPermissions(activity)
        val missingPermissionTypes = getMissingPermissionsType(missingPermissions)
        return missingPermissionTypes.isEmpty()
    }

    fun requestmissPermission(activity: Activity): List<PermissionType> {
        val missingPermissions = getMissingPermissions(activity)
        return getMissingPermissionsType(missingPermissions)
    }

    fun requestPermissionType(activity: Activity): List<PermissionType> {
        val grantedPermissions = getGrantedPermissions(activity)
        val grantedPermissionTypes = getGrantedPermissionsType(grantedPermissions)
        return grantedPermissionTypes
    }

    fun requestPermission(
        activity: Activity,
        requestPermission: Array<String>,
        onAction: () -> Unit,
    ) {
        val missingPermissions = getMissingPermissions(activity, requestPermission)
        Log.e("requestPermission", missingPermissions.toString())
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()){
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        }
        if (missingPermissions.isEmpty()) {
            onAction()
        } else {
            ActivityCompat.requestPermissions(activity, missingPermissions.toTypedArray(), 151)
        }
    }

}

enum class PermissionType {
    CAMERA, STORAGE, LOCATION, BLUETOOTH, CONTACT, NOTIFICATION, CALL_LOG, CALL, NEARBY_DEVICES, SMS, MICROPHONE, UPDATE
}