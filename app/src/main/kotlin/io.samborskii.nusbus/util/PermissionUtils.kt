package io.samborskii.nusbus.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.checkSelfPermission

const val MY_PERMISSION_ACCESS_LOCATION: Int = 1

fun Activity.requestLocationPermissions() {
    if (!isLocationPermissionGranted()) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
            MY_PERMISSION_ACCESS_LOCATION
        )
    }
}

fun Context.isLocationPermissionGranted(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
        (checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
