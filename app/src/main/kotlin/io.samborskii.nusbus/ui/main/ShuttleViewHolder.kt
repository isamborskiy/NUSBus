package io.samborskii.nusbus.ui.main

import android.view.View
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.Shuttle
import kotlinx.android.synthetic.main.list_shuttle.view.*

class ShuttleViewHolder(itemView: View) : AbstractExpandableItemViewHolder(itemView) {

    fun bind(shuttle: Shuttle) {
        itemView.bus_name.text = shuttle.name
        itemView.next_shuttle.text = itemView.resources.getString(R.string.time_next, shuttle.arrivalTime)
        itemView.subsequent_shuttle.text = itemView.resources.getString(R.string.time_next, shuttle.nextArrivalTime)
    }
}
