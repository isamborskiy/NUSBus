package io.samborskii.nusbus.ui.main

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.samborskii.nusbus.R
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.api.NusBusRouteClient
import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService
import io.samborskii.nusbus.model.dao.BusRouteDao
import io.samborskii.nusbus.model.dao.BusStopDao
import io.samborskii.nusbus.model.dao.ShuttleServiceDao
import io.samborskii.nusbus.util.*
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.map.MapPresentationModel
import me.dmdev.rxpm.skipWhileInProgress
import javax.inject.Inject

const val emptyBusStopName: String = ""
const val closeBusStopName: String = "close"
val emptyShuttleService: ShuttleService = ShuttleService("", emptyBusStopName, emptyList())
val closeShuttleService: ShuttleService = ShuttleService("", closeBusStopName, emptyList())

val emptyLatLngZoom: LatLngZoom = LatLngZoom(LatLng(0.0, 0.0), 0f)

const val emptyBusName: String = ""
val emptyBusRoute: List<BusStop> = emptyList()

class MainActivityPresentationModel @Inject constructor(
    private val apiClient: NusBusClient,
    private val routeApiClient: NusBusRouteClient,

    private val busStopDao: BusStopDao,
    private val shuttleServiceDao: ShuttleServiceDao,
    private val busRouteDao: BusRouteDao,

    private val context: Context
) : MapPresentationModel() {

    // states
    val busStopsData = State(emptyList<BusStop>())
    val shuttleServiceData = State(emptyShuttleService)
    val cameraPositionData = State(emptyLatLngZoom)
    val busRouteData = State(emptyBusRoute)

    val onlineStatusData = State(true)

    val inProgress = State(false)

    private val voidData = State<Throwable>()

    // commands
    val errorMessage = Command<MainActivityException>()

    // actions
    val refreshBusStopsAction = Action<Unit>()
    val loadShuttleServiceAction = Action<String>()
    val requestMyLocationAction = Action<Unit>()
    val changeCameraPositionAction = Action<LatLngZoom>()
    val showBusRouteData = Action<String>()

    private val nusBusServerErrorMessage: String = context.getString(R.string.nus_bus_server_error)

    override fun onCreate() {
        super.onCreate()

        refreshBusStopsAction.observable
            .skipWhileInProgress(inProgress.observable)
            .flatMapSingle { loadBusStops() }
            .retry()
            .subscribe(busStopsData.consumer)
            .untilDestroy()

        loadShuttleServiceAction.observable
            .skipWhileInProgress(inProgress.observable)
            .doOnNext { busStopName -> if (busStopName != emptyBusStopName) loadShuttleServiceLocal(busStopName) }
            .flatMapSingle { busStopName ->
                when (busStopName) {
                    emptyBusStopName -> Single.just(emptyShuttleService)
                    closeBusStopName -> Single.just(closeShuttleService)
                    else -> loadShuttleService(busStopName)
                }
            }
            .retry()
            .subscribe(shuttleServiceData.consumer)
            .untilDestroy()

        showBusRouteData.observable
            .skipWhileInProgress(inProgress.observable)
            .doOnNext { busName ->
                if (busName != emptyBusName) loadBusRouteLocal(busName, busStopsData.value, shuttleServiceData.value)
            }
            .flatMapSingle { busName ->
                when (busName) {
                    emptyBusName -> Single.just(emptyBusRoute)
                    else -> loadBusRoute(busName, busStopsData.value, shuttleServiceData.value)
                }
            }
            .retry()
            .subscribe(busRouteData.consumer)
            .untilDestroy()

        Observable.merge(
            changeCameraPositionAction.observable,
            requestMyLocationAction.observable.map { context.requestLocationOnce() }
        ).retry()
            .subscribe(cameraPositionData.consumer)
            .untilDestroy()

        busStopDao.findAll()
            .subscribeOn(Schedulers.io())
            .doAfterTerminate { refreshBusStopsAction.consumer.accept(Unit) }
            .subscribe(busStopsData.consumer)
            .untilDestroy()

        requestMyLocationAction.consumer.accept(Unit)
    }

    private fun loadBusStops(): Single<List<BusStop>> = apiClient.busStops()
        .bindProgress(inProgress.consumer)
        .subscribeOn(Schedulers.io())
        .doOnSuccess { busStopDao.upsert(it) }
        .doOnSuccess { onlineStatusData.consumer.accept(true) }
        .doOnError { errorMessage.consumer.accept(BusStopsLoadingException(nusBusServerErrorMessage)) }
        .doOnError { onlineStatusData.consumer.accept(false) }

    private fun loadShuttleServiceLocal(busStopName: String): Disposable = shuttleServiceDao.findByName(busStopName)
        .subscribeOn(Schedulers.io())
        .subscribe(shuttleServiceData.consumer, voidData.consumer)

    private fun loadShuttleService(busStopName: String): Single<ShuttleService> = apiClient.shuttleService(busStopName)
        .bindProgress(inProgress.consumer)
        .subscribeOn(Schedulers.io())
        .doOnSuccess { shuttleServiceDao.upsert(it) }
        .doOnSuccess { onlineStatusData.consumer.accept(true) }
        .doOnError { errorMessage.consumer.accept(ShuttleLoadingException(nusBusServerErrorMessage)) }
        .doOnError { onlineStatusData.consumer.accept(false) }

    private fun loadBusRouteLocal(
        busName: String,
        busStops: List<BusStop>,
        shuttleService: ShuttleService
    ): Disposable = busRouteDao.findByName(busName)
        .subscribeOn(Schedulers.io())
        .mapToRoute(busName, busStops, shuttleService)
        .subscribe(busRouteData.consumer, voidData.consumer)

    private fun loadBusRoute(
        busName: String,
        busStops: List<BusStop>,
        shuttleService: ShuttleService
    ): Single<List<BusStop>> =
        routeApiClient.busRoute(busName.removeSpecification())
            .bindProgress(inProgress.consumer)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { busRouteDao.upsert(it) }
            .doOnSuccess { onlineStatusData.consumer.accept(true) }
            .doOnError { errorMessage.consumer.accept(BusRouteLoadingException(nusBusServerErrorMessage)) }
            .doOnError { onlineStatusData.consumer.accept(false) }
            .mapToRoute(busName, busStops, shuttleService)

    private fun Single<BusRoute>.mapToRoute(
        busName: String,
        busStops: List<BusStop>,
        shuttleService: ShuttleService
    ): Single<List<BusStop>> =
        map { busRoute -> busRoute.route.mapNotNull { busStops.find(it) } }
            .map { busRoute -> busRoute.dropWhile { !it.deepEquals(shuttleService.name) } }
            .map { busRoute ->
                when (busName.getSpecification()) {
                    // D1 on COM2 bus stop
                    "To BIZ2" -> busRoute.drop(1).dropWhile { !it.deepEquals(shuttleService.name) }
                    // C1 on UTown bus stop
                    "To KRT" -> busRoute.drop(1).dropWhile { !it.deepEquals(shuttleService.name) }
                    else -> busRoute
                }
            }
}
