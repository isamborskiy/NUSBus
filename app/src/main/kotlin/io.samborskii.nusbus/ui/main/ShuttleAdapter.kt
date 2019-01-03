package io.samborskii.nusbus.ui.main

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.Shuttle
import kotlinx.android.synthetic.main.list_item_shuttle_bus.view.*

class ShuttleAdapter(
    private val shuttles: MutableList<Shuttle> = arrayListOf(),
    private val onItemClickCallback: (String) -> Unit = {}
) : RecyclerView.Adapter<ShuttleViewHolder>() {

    fun clean() {
        val size = this.shuttles.size
        this.shuttles.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun updateShuttles(shuttles: List<Shuttle>) {
        this.shuttles.clear()
        this.shuttles += shuttles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ShuttleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_shuttle_bus, parent, false)
        return ShuttleViewHolder(view)
    }

    override fun getItemCount(): Int = shuttles.size

    override fun onBindViewHolder(view: ShuttleViewHolder, index: Int) {
        val last = shuttles.lastIndex == index
        view.bind(shuttles[index], last, onItemClickCallback)
    }
}

class ShuttleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(shuttle: Shuttle, last: Boolean, onItemClickCallback: (String) -> Unit) {
        val darkGray = ResourcesCompat.getColor(itemView.resources, R.color.dark_gray, null)
        val gray = ResourcesCompat.getColor(itemView.resources, R.color.gray, null)
        val lightGray = ResourcesCompat.getColor(itemView.resources, R.color.light_gray, null)

        itemView.shuttle_bus_layout.setOnClickListener { onItemClickCallback(shuttle.name) }

        itemView.next_shuttle_bus.visibility = View.VISIBLE
        itemView.next_shuttle_bus_units.visibility = View.VISIBLE
        itemView.subsequent_shuttle_bus_units.visibility = View.VISIBLE

        itemView.shuttle_bus_name.text = shuttle.name

        when {
            // shuttle was loaded from the database
            shuttle.isCachedData() -> {
                itemView.next_shuttle_bus.visibility = View.GONE
                itemView.subsequent_shuttle_bus.visibility = View.GONE
            }

            // no service of this shuttle
            shuttle.isNoService() -> {
                listOf(
                    itemView.shuttle_bus_name, itemView.next_shuttle_bus_min, itemView.next_shuttle_bus_units,
                    itemView.subsequent_shuttle_bus_min, itemView.subsequent_shuttle_bus_units
                ).forEach { it.setTextColor(lightGray) }

                itemView.subsequent_shuttle_bus.visibility = View.GONE
                itemView.next_shuttle_bus_units.visibility = View.GONE

                itemView.next_shuttle_bus_min.text = itemView.resources.getString(R.string.no_service)
            }

            else -> {
                listOf(itemView.shuttle_bus_name, itemView.next_shuttle_bus_min, itemView.next_shuttle_bus_units)
                    .forEach { it.setTextColor(darkGray) }
                listOf(itemView.subsequent_shuttle_bus_min, itemView.subsequent_shuttle_bus_units)
                    .forEach { it.setTextColor(gray) }

                itemView.subsequent_shuttle_bus.visibility = View.VISIBLE

                itemView.next_shuttle_bus_min.text = shuttle.localizedArrivalTime(itemView.context)
                itemView.subsequent_shuttle_bus_min.text = shuttle.localizedNextArrivalTime((itemView.context))

                if (shuttle.isArriving()) itemView.next_shuttle_bus_units.visibility = View.GONE
                if (shuttle.isNextArriving()) itemView.subsequent_shuttle_bus_units.visibility = View.GONE
            }
        }

        val dividerVisibility = if (last) View.GONE else View.VISIBLE
        itemView.divider.visibility = dividerVisibility
    }

    private fun Shuttle.localizedArrivalTime(context: Context): String = if (!isArriving()) arrivalTime else
        context.getString(R.string.arriving)

    private fun Shuttle.localizedNextArrivalTime(context: Context): String = if (!isNextArriving()) nextArrivalTime else
        context.getString(R.string.arriving)
}
