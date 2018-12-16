package io.samborskii.nusbus.ui.main

import android.animation.ObjectAnimator
import android.view.View
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder
import kotlinx.android.synthetic.main.list_bus_stop.view.*

private const val ROTATION_COLLAPSED: Float = 0f
private const val ROTATION_EXPANDED: Float = 90f

private const val ANIMATION_DURATION: Long = 250

abstract class BusStopGroupViewHolder(itemView: View) : AbstractExpandableItemViewHolder(itemView) {
    abstract fun isExpandable(): Boolean
}

class BusStopHeaderViewHolder(itemView: View) : BusStopGroupViewHolder(itemView) {
    override fun isExpandable(): Boolean = false
}

class BusStopViewHolder(itemView: View) : BusStopGroupViewHolder(itemView) {

    private var busStopData: BusStopData? = null
    private var expanded: Boolean = false

    private var animator: ObjectAnimator? = null

    fun bind(data: BusStopData) {
        itemView.bus_stop_name.text = data.busStop.caption

        if (this.busStopData !== data) {
            animator?.cancel()
            itemView.arrow.rotation = rotation(data.expanded)
        } else if (data.expanded != expanded) {
            animator?.cancel()
            animator = ObjectAnimator.ofFloat(itemView.arrow, View.ROTATION, rotation(data.expanded))
                .setDuration(ANIMATION_DURATION)
                .apply { start() }
        }

        this.busStopData = data
        this.expanded = data.expanded
    }

    override fun isExpandable(): Boolean  = true

    private fun rotation(expanded: Boolean): Float = if (expanded) ROTATION_EXPANDED else ROTATION_COLLAPSED
}
