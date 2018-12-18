package io.samborskii.nusbus.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.samborskii.nusbus.NusBusApplication
import io.samborskii.nusbus.R
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.Shuttle
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.rxpm.base.PmSupportActivity
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val REFRESH_TIME_IN_SECONDS = 15L

class MainActivity : PmSupportActivity<MainPresentationModel>() {

    @Inject
    lateinit var client: NusBusClient

    private lateinit var adapter: BusStopsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        NusBusApplication.getComponent(this).inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = setAdapter()
    }

    private fun setAdapter(): BusStopsAdapter {
        val expMgr = RecyclerViewExpandableItemManager(null)
        val layoutManager = LinearLayoutManager(this)
        val adapter = BusStopsAdapter(layoutManager).apply { setHasStableIds(true) }

        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = expMgr.createWrappedAdapter(adapter)

        (recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        expMgr.attachRecyclerView(recycler_view)
        return adapter
    }

    override fun onBindPresentationModel(pm: MainPresentationModel) {
        pm.busStopsData.observable bindTo { adapter.updateBusStops(it) }
        pm.shuttleServiceData.observable bindTo { adapter.updateBusStopShuttles(it.name, it.shuttles) }

        pm.errorMessage.observable bindTo { Snackbar.make(bus_stops_layout, it, Snackbar.LENGTH_SHORT).show() }

        adapter.getOnGroupItemClickObservable() bindTo pm.loadShuttleService.consumer
        adapter.getExpandedItemsObservable() bindTo pm.loadShuttleService.consumer
    }

    override fun providePresentationModel(): MainPresentationModel = MainPresentationModel(client)

    internal class BusStopsAdapter(layoutManager: LinearLayoutManager) :
        AbstractExpandableItemAdapter<BusStopGroupViewHolder, ShuttleViewHolder>() {

        private val items = mutableListOf<BusStopData>()

        private val groupItemClickSubject = PublishSubject.create<String>()
        private val expandedItemsObservable = Observable.interval(REFRESH_TIME_IN_SECONDS, TimeUnit.SECONDS)
            .flatMap {
                val first = layoutManager.findFirstVisibleItemPosition()
                val last = layoutManager.findLastVisibleItemPosition()

                Observable.fromIterable(
                    (first..last).map { i -> items[i] }
                        .filter { item -> item.expanded }
                        .map { item -> item.busStop.name }
                )
            }

        private val loadShuttlesCallback: (String) -> Unit = { groupItemClickSubject.onNext(it) }

        fun getOnGroupItemClickObservable(): Observable<String> = groupItemClickSubject

        fun getExpandedItemsObservable(): Observable<String> = expandedItemsObservable

        fun updateBusStops(busStops: List<BusStop>) {
            items.clear()
            items += busStops.map { BusStopData(it) }
            notifyDataSetChanged()
            Log.i("TEST", "UPDATED DATA")
        }

        fun updateBusStopShuttles(busStopName: String, shuttles: List<Shuttle>) {
            val busStopIndex = items.indexOfFirst { it.busStop.name == busStopName }
            if (busStopIndex == -1) return

            val busStopData = items[busStopIndex]
            busStopData.shuttles.clear()
            busStopData.shuttles += shuttles
            notifyDataSetChanged()
        }

        override fun getGroupCount(): Int = 1 + items.size

        override fun getChildCount(groupPosition: Int): Int =
            if (groupPosition == 0) 0 else items[groupPosition - 1].shuttles.size

        override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

        override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

        override fun getGroupItemViewType(groupPosition: Int): Int =
            if (groupPosition == 0) HEADER_VIEW_TYPE else BUS_STOP_VIEW_TYPE

        override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int = 0

        override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): BusStopGroupViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == HEADER_VIEW_TYPE) {
                val view = inflater.inflate(R.layout.list_bus_stops_header, parent, false)
                BusStopHeaderViewHolder(view)
            } else {
                val view = inflater.inflate(R.layout.list_bus_stop, parent, false)
                BusStopViewHolder(view)
            }
        }

        override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ShuttleViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val busView = inflater.inflate(R.layout.list_shuttle, parent, false)
            return ShuttleViewHolder(busView)
        }

        override fun onBindGroupViewHolder(holder: BusStopGroupViewHolder, groupPosition: Int, viewType: Int) {
            if (holder is BusStopViewHolder) {
                holder.bind(items[groupPosition - 1], loadShuttlesCallback)
            }
        }

        override fun onBindChildViewHolder(
            holder: ShuttleViewHolder, groupPosition: Int,
            childPosition: Int, viewType: Int
        ) = holder.bind(items[groupPosition - 1].shuttles[childPosition])

        override fun onCheckCanExpandOrCollapseGroup(
            holder: BusStopGroupViewHolder, groupPosition: Int,
            x: Int, y: Int, expand: Boolean
        ): Boolean {
            if (holder is BusStopViewHolder) {
                items[groupPosition - 1].expanded = expand
            }
            return holder.isExpandable()
        }

        companion object {
            private const val HEADER_VIEW_TYPE: Int = 0
            private const val BUS_STOP_VIEW_TYPE: Int = 1
        }
    }
}
