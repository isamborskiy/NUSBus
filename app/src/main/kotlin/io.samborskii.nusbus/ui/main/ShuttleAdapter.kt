package io.samborskii.nusbus.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.samborskii.nusbus.R
import io.samborskii.nusbus.model.Shuttle
import kotlinx.android.synthetic.main.list_shuttle.view.*

class ShuttleAdapter(private val shuttles: List<Shuttle>) : RecyclerView.Adapter<ShuttleViewHolder>() {

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
        itemView.shuttle_name.text = shuttle.name
        itemView.next_shuttle_min.text = shuttle.arrivalTime
        itemView.subsequent_shuttle_min.text = shuttle.nextArrivalTime

        val dividerVisibility = if (last) View.GONE else View.VISIBLE
        itemView.divider.visibility = dividerVisibility
    }
}
