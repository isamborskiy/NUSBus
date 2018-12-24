package io.samborskii.nusbus.ui.main

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.Shuttle
import kotlinx.android.synthetic.main.list_shuttle.view.*

class ShuttleAdapter(
    private val shuttles: MutableList<Shuttle> = arrayListOf()
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
        val view = inflater.inflate(R.layout.list_shuttle, parent, false)
        return ShuttleViewHolder(view)
    }

    override fun getItemCount(): Int = shuttles.size

    override fun onBindViewHolder(view: ShuttleViewHolder, index: Int) {
        val last = shuttles.lastIndex == index
        view.bind(shuttles[index], last)
    }
}

class ShuttleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(shuttle: Shuttle, last: Boolean) {
        val darkGray = ResourcesCompat.getColor(itemView.resources, R.color.dark_gray, null)
        val gray = ResourcesCompat.getColor(itemView.resources, R.color.gray, null)
        val lightGray = ResourcesCompat.getColor(itemView.resources, R.color.light_gray, null)

        itemView.next_shuttle_units.visibility = View.VISIBLE
        itemView.subsequent_shuttle_units.visibility = View.VISIBLE

        if (shuttle.isNoService()) {
            // no service of this shuttle
            listOf(
                itemView.shuttle_name, itemView.next_shuttle_min, itemView.next_shuttle_units,
                itemView.subsequent_shuttle_min, itemView.subsequent_shuttle_units
            ).forEach { it.setTextColor(lightGray) }

            itemView.subsequent_shuttle.visibility = View.GONE
            itemView.next_shuttle_units.visibility = View.GONE

            itemView.shuttle_name.text = shuttle.name
            itemView.next_shuttle_min.text = itemView.resources.getString(R.string.no_service)
        } else {
            listOf(itemView.shuttle_name, itemView.next_shuttle_min, itemView.next_shuttle_units)
                .forEach { it.setTextColor(darkGray) }
            listOf(itemView.subsequent_shuttle_min, itemView.subsequent_shuttle_units)
                .forEach { it.setTextColor(gray) }

            itemView.subsequent_shuttle.visibility = View.VISIBLE

            itemView.shuttle_name.text = shuttle.name
            itemView.next_shuttle_min.text = shuttle.arrivalTime
            itemView.subsequent_shuttle_min.text = shuttle.nextArrivalTime

            if (shuttle.isArriving()) itemView.next_shuttle_units.visibility = View.GONE
            if (shuttle.isNextArriving()) itemView.subsequent_shuttle_units.visibility = View.GONE
        }

        val dividerVisibility = if (last) View.GONE else View.VISIBLE
        itemView.divider.visibility = dividerVisibility
    }
}
