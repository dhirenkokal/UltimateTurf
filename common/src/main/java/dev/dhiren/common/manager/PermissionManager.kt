package dev.dhiren.common.manager

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import dev.dhiren.common.resource.enums.RationaleType

class PermissionManager(private val activity: Activity) {

    fun requestForPermission(
        permission: String,
        isFirstTime: Boolean,
        permissionAvailable: () -> Unit = {},
        requestForPermission: () -> Unit,
    ): RationaleType {
        if (havePermission(permission = permission)) {
            permissionAvailable.invoke()
            return RationaleType.NONE
        } else {
            return when {
                isFirstTime -> {
                    requestForPermission.invoke()
                    RationaleType.NONE
                }

                else -> {
                    //user denied it twice
                    /*showSettingsDialog(
                                title = settingsDialogTitle,
                                msg = settingsDialogMessage,
                            )*/
                    RationaleType.WITH_SETTINGS
                }
            }
        }

    }

    fun havePermission(permission: String): Boolean {
        val value = ActivityCompat.checkSelfPermission(activity, permission)
        return value == PackageManager.PERMISSION_GRANTED
    }

    fun gotoSettings() {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:${activity.packageName}")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        activity.startActivity(i)
    }

    fun isBlePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            havePermission(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            true
        }
    }

    fun isLocationPermissionsGranted(): Boolean {
        return havePermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun isBluetoothEnabled(): Boolean {
        val manager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter
        return adapter != null && manager.adapter.isEnabled
    }

    fun isCameraPermissionGranted(): Boolean {
        return havePermission(Manifest.permission.CAMERA)
    }

    fun isLocationEnabled(): Boolean {
        val lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkProviderEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isNetworkProviderEnabled || isGpsEnabled
    }

    fun isPushNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            havePermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }

    fun isLocationPermissionsGrantedForFurnitureSDK(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            true
        } else {
            isLocationPermissionsGranted()
        }
    }

    fun canWeUseUltimateTurfApp(): Boolean {
        return isBlePermissionGranted() && isBluetoothEnabled() && isCameraPermissionGranted()
//                && isLocationEnabled()
//                && isLocationPermissionsGrantedForFurnitureSDK()
    }

    fun areAllRequiredPermissionPresent(): Boolean {
        return canWeUseUltimateTurfApp() && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || AppConnectivityManager.pushNotificationPermissionFlow.value)
    }


}
