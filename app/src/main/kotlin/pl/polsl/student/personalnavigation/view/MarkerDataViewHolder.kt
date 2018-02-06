package pl.polsl.student.personalnavigation.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.DistanceMarker


class MarkerDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameTextView = itemView.findViewById<TextView>(R.id.markerDataNameTextView)

    var marker: DistanceMarker? = null
        set(value) {
            field = value
            nameTextView.text = value?.name ?: ""
        }
}