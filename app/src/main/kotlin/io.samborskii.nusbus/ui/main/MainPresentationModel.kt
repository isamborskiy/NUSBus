package io.samborskii.nusbus.ui.main

import io.reactivex.schedulers.Schedulers
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStop
import me.dmdev.rxpm.PresentationModel

class MainPresentationModel(private val apiClient: NusBusClient) : PresentationModel() {

    // states
    val data = State<List<BusStop>>(emptyList())

    // commands
    val errorMessage = Command<String>()

    // actions
    val refreshAction = Action<Unit>()

    override fun onCreate() {
        super.onCreate()

        refreshAction.observable
            .flatMapSingle { _ ->
                apiClient.busStops()
                    .subscribeOn(Schedulers.io())
                    .map { response -> response.busStopsResult.busStops }
                    .doOnError { _ -> errorMessage.consumer.accept("Loading data error") }
            }
            .retry()
            .subscribe(data.consumer)
            .untilDestroy()

        refreshAction.consumer.accept(Unit)
    }
}
