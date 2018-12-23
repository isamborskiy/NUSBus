package io.samborskii.nusbus.ui.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.samborskii.nusbus.NusBusApplication
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.Shuttle
import io.samborskii.nusbus.ui.anim.EndActionListener
import io.samborskii.nusbus.ui.anim.heightToAnimator
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.rxpm.map.base.MapPmSupportActivity


private const val ANIMATION_DURATION: Long = 200L

class MainActivity : MapPmSupportActivity<MainPresentationModel>() {

    private var headerHeight: Int = 0
    private var shuttleCardMaxHeight: Int = 0
    private var shuttleListItemHeight: Int = 0
    private var shuttleListItemDividerHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headerHeight = resources.getDimension(R.dimen.bus_stop_header_height).toInt()
        shuttleCardMaxHeight = resources.getDimension(R.dimen.shuttle_card_max_height).toInt()
        shuttleListItemHeight = resources.getDimension(R.dimen.shuttle_list_item_height).toInt()
        shuttleListItemDividerHeight = resources.getDimension(R.dimen.shuttle_list_item_divider_height).toInt()

        close_bus_stop.setOnClickListener { hideBusStopInformation() }

        val layoutManager = LinearLayoutManager(this)
        shuttle_list.layoutManager = layoutManager
        shuttle_list.adapter = ShuttleAdapter()

        showBusStopInformation(BusStop("UTown", "University Town", 0.0, 0.0))
    }

    override fun onBindMapPresentationModel(pm: MainPresentationModel, googleMap: GoogleMap) {
        pm.busStopsData.observable bindTo { showBusStopsOnMap(it, googleMap) }
    }

    override fun onBindPresentationModel(pm: MainPresentationModel) {
//        pm.shuttleServiceData.observable bindTo { adapter.updateBusStopShuttles(it.name, it.shuttles) }

        pm.errorMessage.observable bindTo { Snackbar.make(main_layout, it, Snackbar.LENGTH_SHORT).show() }
    }

    override fun providePresentationModel(): MainPresentationModel =
        NusBusApplication.getComponent(this).newMainPresentationModel()

    private fun showBusStopsOnMap(busStops: List<BusStop>, googleMap: GoogleMap) {
        val markers = busStops.map {
            MarkerOptions()
                .position(LatLng(it.latitude, it.longitude))
                .title(it.caption)
        }

        markers.forEach { googleMap.addMarker(it) }
    }

    private fun showBusStopInformation(busStop: BusStop) {
        val shuttles = listOf(
            Shuttle("D1", "5", "14", "-", "-"),
            Shuttle("D2", "13", "22", "-", "-")
        )

        bus_stop_name.text = busStop.caption
        (shuttle_list.adapter as ShuttleAdapter).updateShuttles(shuttles)

        openInformationPanels(shuttles.size)
    }

    private fun hideBusStopInformation() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            heightToAnimator(header, 0, ANIMATION_DURATION),
            heightToAnimator(shuttle_card, 0, ANIMATION_DURATION)
        )
        animatorSet.addListener(object : EndActionListener() {
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
        heightToAnimator(shuttle_card, shuttleCardHeight, ANIMATION_DURATION).start()
    }
}
