package io.samborskii.nusbus.ui.main

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.samborskii.nusbus.R
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService
import io.samborskii.nusbus.util.LatLngZoom
import io.samborskii.nusbus.util.requestLocationOnce
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.map.MapPresentationModel
import me.dmdev.rxpm.skipWhileInProgress
import javax.inject.Inject

const val emptyBusStopName: String = ""
val emptyShuttleService: ShuttleService = ShuttleService("", emptyBusStopName, emptyList())

val emptyLatLngZoom: LatLngZoom = LatLngZoom(LatLng(0.0, 0.0), 0f)

class MainActivityPresentationModel @Inject constructor(
    private val apiClient: NusBusClient,
    private val context: Context
) : MapPresentationModel() {

    // states
    val busStopsData = State(emptyList<BusStop>())
    val shuttleServiceData = State(emptyShuttleService)
    val cameraPositionData = State(emptyLatLngZoom)

    val inProgress = State(false)

    // commands
    val errorMessage = Command<MainActivityException>()

    // actions
    val refreshBusStopsAction = Action<Unit>()
    val loadShuttleServiceAction = Action<String>()
    val requestMyLocationAction = Action<Unit>()
    val changeCameraPositionAction = Action<LatLngZoom>()

    override fun onCreate() {
        super.onCreate()

        refreshBusStopsAction.observable
            .skipWhileInProgress(inProgress.observable)
            .flatMapSingle {
                apiClient.busStops()
                    .bindProgress(inProgress.consumer)
                    .subscribeOn(Schedulers.io())
                    .doOnError {
                        errorMessage.consumer.accept(
                            BusStopsLoadingException(context.getString(R.string.nus_bus_server_error))
                        )
                    }
            }
            .retry()
            .subscribe(busStopsData.consumer)
            .untilDestroy()

        loadShuttleServiceAction.observable
            .skipWhileInProgress(inProgress.observable)
            .flatMapSingle { busStopName ->
                if (busStopName == emptyBusStopName) {
                    Single.just(emptyShuttleService)
                } else {
                    apiClient.shuttleService(busStopName)
                        .bindProgress(inProgress.consumer)
                        .subscribeOn(Schedulers.io())
                        .doOnError {
                            errorMessage.consumer.accept(
                                ShuttleLoadingException(context.getString(R.string.nus_bus_server_error))
                            )
                        }
                }
            }
            .retry()
            .subscribe(shuttleServiceData.consumer)
            .untilDestroy()

        Observable.merge(
            changeCameraPositionAction.observable,
            requestMyLocationAction.observable.map { context.requestLocationOnce() }
        ).retry()
            .subscribe(cameraPositionData.consumer)
            .untilDestroy()

        refreshBusStopsAction.consumer.accept(Unit)
        requestMyLocationAction.consumer.accept(Unit)
    }
}
