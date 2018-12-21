package io.samborskii.nusbus.ui.main

import io.reactivex.schedulers.Schedulers
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPresentationModel
import javax.inject.Inject

class MainPresentationModel @Inject constructor(private val apiClient: NusBusClient) : MapPresentationModel() {

    // states
    val busStopsData = State<List<BusStop>>(emptyList())
    val shuttleServiceData = State<ShuttleService>()

    // commands
    val errorMessage = Command<String>()

    // actions
    val refreshAction = Action<Unit>()
    val loadShuttleService = Action<String>()

    override fun onCreate() {
        super.onCreate()

        refreshAction.observable
            .flatMapSingle {
                apiClient.busStops()
                    .subscribeOn(Schedulers.io())
                    .doOnError { errorMessage.consumer.accept("Loading data error") }
            }
            .retry()
            .subscribe(busStopsData.consumer)
            .untilDestroy()

        loadShuttleService.observable
            .flatMapSingle { busStopName ->
                apiClient.shuttleService(busStopName)
                    .subscribeOn(Schedulers.io())
                    .doOnError { errorMessage.consumer.accept("Cannot load shuttle service") }
            }
            .retry()
            .subscribe(shuttleServiceData.consumer)
            .untilDestroy()

        refreshAction.consumer.accept(Unit)
    }
}
