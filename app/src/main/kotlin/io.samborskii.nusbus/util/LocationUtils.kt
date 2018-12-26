package io.samborskii.nusbus.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

private const val DEFAULT_ZOOM: Float = 15f

// NUS location
private val nusLatLng: LatLng = LatLng(1.2955364, 103.7737544)
private val defaultLatLng: LatLng = LatLng(0.0, 0.0)

@SuppressLint("MissingPermission")
fun Activity.enableGoogleMapLocation(googleMap: GoogleMap?) {
    if (isLocationPermissionGranted() && googleMap != null) googleMap.isMyLocationEnabled = true
}

@SuppressLint("MissingPermission")
fun Context.requestLocationOnce(): LatLngZoom {
    if (isLocationPermissionGranted()) {
        val locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val lastLocation = when {
            locationManager.isGpsEnabled() -> locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            locationManager.isNetworkEnabled() -> locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            else -> null
        }
        if (lastLocation != null) return LatLngZoom(lastLocation.toLatLng(), DEFAULT_ZOOM)
    }
    // if lastLocation is null or location permission is not granted
    return LatLngZoom(nusLatLng, DEFAULT_ZOOM)
}

fun Context.isGpsEnabled(): Boolean =
    (getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager).isGpsEnabled()

fun Context.isNetworkEnabled(): Boolean =
    (getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager).isNetworkEnabled()

private fun LocationManager.isGpsEnabled(): Boolean = isProviderEnabled(LocationManager.GPS_PROVIDER)

private fun LocationManager.isNetworkEnabled(): Boolean = isProviderEnabled(LocationManager.NETWORK_PROVIDER)

fun LatLng.isDefaultLocation(): Boolean = this == defaultLatLng

data class LatLngZoom(val latLng: LatLng, val zoom: Float)

private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)
