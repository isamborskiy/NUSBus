package io.samborskii.nusbus.ui.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.PublishSubject
import io.samborskii.nusbus.NusBusApplication
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService
import io.samborskii.nusbus.ui.anim.AnimationEndListener
import io.samborskii.nusbus.ui.anim.heightToAnimator
import io.samborskii.nusbus.ui.anim.topMarginToAnimator
import io.samborskii.nusbus.util.*
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.rxpm.map.base.MapPmSupportActivity

private const val ANIMATION_DURATION: Long = 200L

class MainActivity : MapPmSupportActivity<MainActivityPresentationModel>(),
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private var headerHeight: Int = 0
    private var shuttleCardMinHeight: Int = 0
    private var shuttleCardMaxHeight: Int = 0
    private var shuttleListItemHeight: Int = 0
    private var shuttleListItemDividerHeight: Int = 0
    private var offlineCardHeight: Int = 0

    private var statusBarHeight: Int = 0
    private var progressBarMargin: Int = 0

    private lateinit var markerBitmap: Bitmap

    private val markers: MutableMap<String, Marker> = HashMap()
    private var routePolyline: Polyline? = null

    private val refreshShuttleServiceSubject = PublishSubject.create<Unit>()
    private val selectMarkerSubject = PublishSubject.create<String>()
    private val changeCameraPositionSubject = PublishSubject.create<LatLngZoom>()
    private val buildBusRouteSubject = PublishSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headerHeight = resources.getDimension(R.dimen.bus_stop_header_height).toInt()
        shuttleCardMinHeight = resources.getDimension(R.dimen.shuttles_panel_min_height).toInt()
        shuttleCardMaxHeight = resources.getDimension(R.dimen.shuttles_panel_max_height).toInt()
        shuttleListItemHeight = resources.getDimension(R.dimen.shuttle_bus_list_item_height).toInt()
        shuttleListItemDividerHeight = resources.getDimension(R.dimen.shuttle_bus_list_item_divider_height).toInt()
        offlineCardHeight = resources.getDimension(R.dimen.online_status_height).toInt()

        statusBarHeight = resources.getDimension(R.dimen.bus_stop_header_top_padding).toInt()
        progressBarMargin = resources.getDimension(R.dimen.progress_bar_margin).toInt()

        markerBitmap = loadMarkerBitmap()

        close_bus_stop.setOnClickListener {
            selectMarkerSubject.onNext(emptyBusStopName)
            buildBusRouteSubject.onNext(emptyBusName)
        }

        swipe_refresh_layout.setColorSchemeResources(R.color.primary, R.color.primaryDark)
        swipe_refresh_layout.setOnRefreshListener {
            refreshShuttleServiceSubject.onNext(Unit)
            swipe_refresh_layout.isRefreshing = false
        }

        val layoutManager = LinearLayoutManager(this)
        shuttle_list.layoutManager = layoutManager
        shuttle_list.adapter = ShuttleAdapter(onItemClickCallback = buildBusRouteSubject::onNext)
    }

    override fun onPause() {
        val latLngZoom = googleMap?.cameraPosition?.let { LatLngZoom(it.target, it.zoom) } ?: emptyLatLngZoom
        changeCameraPositionSubject.onNext(latLngZoom)

        super.onPause()
    }

    override fun onBackPressed() {
        selectMarkerSubject.onNext(closeBusStopName)
        buildBusRouteSubject.onNext(emptyBusName)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_ACCESS_LOCATION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    enableGoogleMapLocation(googleMap)
                    changeCameraPositionSubject.onNext(requestLocationOnce())
                } else {
                    Toast.makeText(this, R.string.location_permission_is_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBindMapPresentationModel(pm: MainActivityPresentationModel, googleMap: GoogleMap) {
        setupMaps(googleMap)

        pm.busStopsData.observable bindTo { showBusStopsOnMap(it, googleMap, pm.shuttleServiceData.value) }
        pm.cameraPositionData.observable bindTo { googleMap.moveCamera(it) }
        pm.busRouteData.observable bindTo { showBusRouteOnMap(it, googleMap) }
    }

    override fun onBindPresentationModel(pm: MainActivityPresentationModel) {
        pm.shuttleServiceData.observable bindTo { updateBusStopInformation(it) }
        pm.errorMessage.observable bindTo { handleErrorMessage(it) }
        pm.onlineStatusData.observable bindTo { updateOnlineStatus(it) }

        pm.inProgress.observable bindTo { loading.visibility = if (it) View.VISIBLE else View.GONE }

        selectMarkerSubject bindTo pm.loadShuttleServiceAction
        refreshShuttleServiceSubject.map { pm.shuttleServiceData.value.name } bindTo pm.loadShuttleServiceAction
        changeCameraPositionSubject bindTo pm.changeCameraPositionAction
        buildBusRouteSubject bindTo pm.showBusRouteData

        offline_notification.clicks() bindTo pm.refreshBusStopsAction
        my_location.clicks().filter { isPermissionGrantedAndGpsEnabled() } bindTo pm.requestMyLocationAction
    }

    override fun providePresentationModel(): MainActivityPresentationModel =
        NusBusApplication.getComponent(this).newMainPresentationModel()

    override fun onMarkerClick(marker: Marker): Boolean {
        val busStopName = marker.tag as String
        selectMarkerSubject.onNext(busStopName)
        buildBusRouteSubject.onNext(emptyBusName)
        return false
    }

    override fun onMapClick(latLng: LatLng) {
        selectMarkerSubject.onNext(emptyBusStopName)
        buildBusRouteSubject.onNext(emptyBusName)
    }

    private fun setupMaps(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener(this)
        googleMap.uiSettings.apply {
            isCompassEnabled = false
            isMapToolbarEnabled = false
            isMyLocationButtonEnabled = false
        }
        enableGoogleMapLocation(googleMap)
    }

    private fun showBusStopsOnMap(busStops: List<BusStop>, googleMap: GoogleMap, shuttleService: ShuttleService) {
        googleMap.clear()

        markers.clear()
        markers += busStops.associate {
            val markerOptions = MarkerOptions()
                .position(it.toLatLng())
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
            val marker = googleMap.addMarker(markerOptions).apply { tag = it.name }
            it.name to marker
        }
        markers[shuttleService.name]?.setIcon(null)
    }

    private fun showBusRouteOnMap(route: List<BusStop>, googleMap: GoogleMap) {
        routePolyline?.remove()
        routePolyline = null

        val polylineOptions = PolylineOptions()
            .color(ContextCompat.getColor(this, R.color.route))
            .addAll(route.map { it.toLatLng() })
        routePolyline = googleMap.addPolyline(polylineOptions)
    }

    private fun handleErrorMessage(exc: MainActivityException) {
        when (exc) {
            is BusStopsLoadingException -> Toast.makeText(this, exc.localizedMessage, Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, exc.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateOnlineStatus(online: Boolean) =
        heightToAnimator(offline_notification, if (online) 0 else offlineCardHeight, ANIMATION_DURATION).start()

    private fun isPermissionGrantedAndGpsEnabled(): Boolean {
        val permissionGranted = isLocationPermissionGranted()
        if (!permissionGranted) requestLocationPermissions()

        val gpsEnabled = isGpsEnabled()
        if (permissionGranted && !gpsEnabled) {
            Toast.makeText(this, R.string.turn_on_device_location, Toast.LENGTH_SHORT).show()
        }

        return permissionGranted && gpsEnabled
    }

    private fun updateBusStopInformation(shuttleService: ShuttleService) {
        when (shuttleService) {
            emptyShuttleService -> hideBusStopInformation()
            closeShuttleService -> tryToFinishActivity()
            else -> showBusStopInformation(shuttleService)
        }
    }

    private fun showBusStopInformation(shuttleService: ShuttleService) {
        val (busStopCaption, busStopName, shuttles) = shuttleService
        markers.forEach { (name, marker) ->
            if (name == busStopName) {
                marker.setIcon(null)
            } else {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
            }
        }

        bus_stop_name.text = busStopCaption
        (shuttle_list.adapter as ShuttleAdapter).updateShuttles(shuttles ?: emptyList())

        var shuttleCardHeight = (shuttles?.size ?: 0) * (shuttleListItemHeight + shuttleListItemDividerHeight)
        shuttleCardHeight = shuttleCardHeight.coerceIn(shuttleCardMinHeight..shuttleCardMaxHeight)

        heightToAnimator(header, headerHeight, ANIMATION_DURATION).start()
        topMarginToAnimator(loading, progressBarMargin, ANIMATION_DURATION).start()
        heightToAnimator(shuttle_card, shuttleCardHeight, ANIMATION_DURATION).start()
    }

    private fun hideBusStopInformation() {
        markers.forEach { (_, marker) -> marker.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap)) }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            heightToAnimator(header, 0, ANIMATION_DURATION),
            heightToAnimator(shuttle_card, 0, ANIMATION_DURATION),
            topMarginToAnimator(loading, statusBarHeight + progressBarMargin, ANIMATION_DURATION)
        )
        animatorSet.addListener(object : AnimationEndListener {
            override fun onAnimationEnd(animation: Animator?) {
                (shuttle_list.adapter as ShuttleAdapter).clean()
                bus_stop_name.text = ""
            }
        })
        animatorSet.start()
    }

    private fun tryToFinishActivity() {
        if (shuttle_card.height > 0) {
            hideBusStopInformation()
        } else {
            finish()
        }
    }
}

private fun Context.loadMarkerBitmap(): Bitmap {
    val size = resources.getDimensionPixelSize(R.dimen.marker_size)
    val markerBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    val shape = ResourcesCompat.getDrawable(resources, R.drawable.maps_marker, null)
    shape!!.setBounds(0, 0, markerBitmap.width, markerBitmap.height)
    shape.draw(Canvas(markerBitmap))

    return markerBitmap
}

private fun GoogleMap.moveCamera(latLngZoom: LatLngZoom) {
    if (latLngZoom != emptyLatLngZoom) {
        val zoom = maxOf(cameraPosition.zoom, latLngZoom.zoom)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngZoom.latLng, zoom)
        if (cameraPosition.target.isDefaultLocation()) moveCamera(cameraUpdate) else animateCamera(cameraUpdate)
    }
}

private fun BusStop.toLatLng(): LatLng = LatLng(latitude, longitude)
