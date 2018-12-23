package io.samborskii.nusbus.ui.main

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
import io.samborskii.nusbus.ui.anim.ResizeAnimation
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.rxpm.map.base.MapPmSupportActivity


private const val ANIMATION_DURATION: Long = 200L

class MainActivity : MapPmSupportActivity<MainPresentationModel>() {

    private var headerHeight: Int = 0
    private var shuttleCardHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headerHeight = resources.getDimension(R.dimen.bus_stop_header_height).toInt()
        shuttleCardHeight = resources.getDimension(R.dimen.shuttle_card_height).toInt()

        val layoutManager = LinearLayoutManager(this)
        shuttle_list.layoutManager = layoutManager
        shuttle_list.adapter = ShuttleAdapter(
            listOf(
                Shuttle("D1", "-", "-", "-", "-"),
                Shuttle("D2", "-", "-", "-", "-")
            )
        )

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
        bus_stop_name.text = busStop.caption

        openInformationPanels()
    }

    private fun openInformationPanels() {
        val headerAnim = ResizeAnimation(header, 0, headerHeight, ANIMATION_DURATION)
        val shuttleCardAnim = ResizeAnimation(shuttle_card, 0, shuttleCardHeight, ANIMATION_DURATION)

        header.startAnimation(headerAnim)
        shuttle_card.startAnimation(shuttleCardAnim)
    }
}
