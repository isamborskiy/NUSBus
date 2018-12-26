package io.samborskii.nusbus.ui.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.samborskii.nusbus.NusBusApplication
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.Shuttle
import io.samborskii.nusbus.ui.anim.AnimationEndListener
import io.samborskii.nusbus.ui.anim.heightToAnimator
import io.samborskii.nusbus.ui.anim.topMarginToAnimator
import io.samborskii.nusbus.util.*
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.rxpm.map.base.MapPmSupportActivity

private const val ANIMATION_DURATION: Long = 200L

private val emptyLatLngZoom: LatLngZoom = LatLngZoom(LatLng(0.0, 0.0), 0f)

class MainActivity : MapPmSupportActivity<MainPresentationModel>(),
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private var headerHeight: Int = 0
    private var shuttleCardMaxHeight: Int = 0
    private var shuttleListItemHeight: Int = 0
    private var shuttleListItemDividerHeight: Int = 0

    private var statusBarHeight: Int = 0
    private var progressBarMargin: Int = 0

    private lateinit var markerBitmap: Bitmap

    private var selectedMarker: Marker? = null

    private val markerClickSubject = PublishSubject.create<String>()
    private val cameraPositionSubject = BehaviorSubject.create<LatLngZoom>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestLocationPermissions()

        headerHeight = resources.getDimension(R.dimen.bus_stop_header_height).toInt()
        shuttleCardMaxHeight = resources.getDimension(R.dimen.shuttle_card_max_height).toInt()
        shuttleListItemHeight = resources.getDimension(R.dimen.shuttle_list_item_height).toInt()
        shuttleListItemDividerHeight = resources.getDimension(R.dimen.shuttle_list_item_divider_height).toInt()

        statusBarHeight = resources.getDimension(R.dimen.bus_stop_header_top_padding).toInt()
        progressBarMargin = resources.getDimension(R.dimen.progress_bar_margin).toInt()

        markerBitmap = loadMarkerBitmap()

        close_bus_stop.setOnClickListener { hideBusStopInformation() }

        val layoutManager = LinearLayoutManager(this)
        shuttle_list.layoutManager = layoutManager
        shuttle_list.adapter = ShuttleAdapter()
    }

    override fun onPause() {
        val latLngZoom = googleMap?.cameraPosition?.let { LatLngZoom(it.target, it.zoom) } ?: emptyLatLngZoom
        cameraPositionSubject.onNext(latLngZoom)

        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_ACCESS_LOCATION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    enableGoogleMapLocation(googleMap)
                }
                requestLocationOnce()
            }
        }
    }

    override fun onBindMapPresentationModel(pm: MainPresentationModel, googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener(this)
        googleMap.uiSettings.apply {
            isCompassEnabled = false
            isMapToolbarEnabled = false
            isMyLocationButtonEnabled = false
        }
        enableGoogleMapLocation(googleMap)

        pm.busStopsData.observable bindTo { showBusStopsOnMap(it, googleMap) }
        pm.cameraPositionData.observable bindTo { googleMap.moveCamera(it) }
    }

    override fun onBindPresentationModel(pm: MainPresentationModel) {
        pm.shuttleServiceData.observable bindTo { showBusStopInformation(it.caption, it.shuttles) }
        pm.errorMessage.observable bindTo { Snackbar.make(main_layout, it, Snackbar.LENGTH_SHORT).show() }

        pm.inProgress.observable bindTo { loading.visibility = if (it) View.VISIBLE else View.GONE }

        markerClickSubject bindTo pm.loadShuttleServiceAction
        cameraPositionSubject bindTo pm.cameraPositionAction
        refresh_shuttle.clicks().map { selectedMarker?.tag as String } bindTo pm.loadShuttleServiceAction.consumer
        my_location.clicks() bindTo pm.myLocationAction.consumer
    }

    override fun providePresentationModel(): MainPresentationModel =
        NusBusApplication.getComponent(this).newMainPresentationModel()

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker != selectedMarker) selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        selectedMarker = marker
        marker.setIcon(null)

        val busStopName = marker.tag as String
        markerClickSubject.onNext(busStopName)
        return false
    }

    override fun onMapClick(latLng: LatLng) = hideBusStopInformation()

    private fun showBusStopsOnMap(busStops: List<BusStop>, googleMap: GoogleMap) {
        googleMap.clear()

        busStops.forEach {
            val markerOptions = MarkerOptions()
                .position(it.toLatLng())
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
            val marker = googleMap.addMarker(markerOptions)
            marker.tag = it.name
        }
    }

    private fun showBusStopInformation(busStopCaption: String, shuttles: List<Shuttle>) {
        bus_stop_name.text = busStopCaption
        (shuttle_list.adapter as ShuttleAdapter).updateShuttles(shuttles)

        openInformationPanels(shuttles.size)
    }

    private fun hideBusStopInformation() {
        selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        selectedMarker = null

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

    private fun openInformationPanels(shuttlesNum: Int) {
        val shuttleCardHeight = minOf(
            shuttleCardMaxHeight,
            shuttlesNum * shuttleListItemHeight + (shuttlesNum - 1) * shuttleListItemDividerHeight
        )

        heightToAnimator(header, headerHeight, ANIMATION_DURATION).start()
        topMarginToAnimator(loading, progressBarMargin, ANIMATION_DURATION).start()
        heightToAnimator(shuttle_card, shuttleCardHeight, ANIMATION_DURATION).start()
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
        animateCamera(CameraUpdateFactory.newLatLngZoom(latLngZoom.latLng, zoom))
    }
}

private fun BusStop.toLatLng(): LatLng = LatLng(latitude, longitude)
