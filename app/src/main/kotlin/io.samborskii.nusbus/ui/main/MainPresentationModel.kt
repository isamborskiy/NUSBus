package io.samborskii.nusbus.ui.main

import android.content.Context
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService
import io.samborskii.nusbus.util.LatLngZoom
import io.samborskii.nusbus.util.requestLocationOnce
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.map.MapPresentationModel
import me.dmdev.rxpm.skipWhileInProgress
import javax.inject.Inject

class MainPresentationModel @Inject constructor(
    private val apiClient: NusBusClient,
    private val context: Context
) : MapPresentationModel() {

    // states
    val busStopsData = State<List<BusStop>>(emptyList())
    val shuttleServiceData = State<ShuttleService>()
    val cameraPositionData = State<LatLngZoom>()

    val inProgress = State(false)

    // commands
    val errorMessage = Command<String>()

    // actions
    val refreshAction = Action<Unit>()
    val loadShuttleServiceAction = Action<String>()
    val myLocationAction = Action<Unit>()
    val cameraPositionAction = Action<LatLngZoom>()

    override fun onCreate() {
        super.onCreate()

        refreshAction.observable
            .skipWhileInProgress(inProgress.observable)
            .flatMapSingle {
                apiClient.busStops()
                    .bindProgress(inProgress.consumer)
                    .subscribeOn(Schedulers.io())
                    .doOnError { errorMessage.consumer.accept("Loading data error") }
            }
            .retry()
            .subscribe(busStopsData.consumer)
            .untilDestroy()

        loadShuttleServiceAction.observable
            .skipWhileInProgress(inProgress.observable)
            .flatMapSingle { busStopName ->
                apiClient.shuttleService(busStopName)
                    .bindProgress(inProgress.consumer)
                    .subscribeOn(Schedulers.io())
                    .doOnError { errorMessage.consumer.accept("Cannot load shuttle service") }
            }
            .retry()
            .subscribe(shuttleServiceData.consumer)
            .untilDestroy()

        Observable.merge(
            cameraPositionAction.observable,
            myLocationAction.observable.map { context.requestLocationOnce() }
        ).retry()
            .subscribe(cameraPositionData.consumer)
            .untilDestroy()

        refreshAction.consumer.accept(Unit)
        myLocationAction.consumer.accept(Unit)
    }
}
