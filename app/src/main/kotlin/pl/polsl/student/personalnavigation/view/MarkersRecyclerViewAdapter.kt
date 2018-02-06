package pl.polsl.student.personalnavigation.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.layoutInflater
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.DistanceMarker


class MarkersRecyclerViewAdapter(private val onClick: (DistanceMarker) -> Unit): RecyclerView.Adapter<MarkerDataViewHolder>() {
    var markers: List<DistanceMarker> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: MarkerDataViewHolder, position: Int) {
        holder.marker = markers[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerDataViewHolder {
        val view = parent.context.layoutInflater.inflate(R.layout.marker_data_layout, parent, false)
        return MarkerDataViewHolder(view, onClick)
    }

    override fun getItemCount(): Int = markers.size
}