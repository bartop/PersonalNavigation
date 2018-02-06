package pl.polsl.student.personalnavigation.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.sdk25.coroutines.onClick
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.DistanceMarker
import pl.polsl.student.personalnavigation.model.Gender
import pl.polsl.student.personalnavigation.model.Skill
import kotlin.math.roundToLong


class MarkerDataViewHolder(
        itemView: View,
        show: (DistanceMarker) -> Unit,
        track: (DistanceMarker) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val nameTextView = itemView.findViewById<TextView>(R.id.markerDataNameTextView)
    private val distanceTextView = itemView.findViewById<TextView>(R.id.markerDistanceTextView)
    private val genderImage = itemView.findViewById<ImageView>(R.id.genderImageView)
    private val stars = mapOf(
            Skill.Low to R.id.starLow,
            Skill.Medium to R.id.starMedium,
            Skill.High to R.id.startHigh
    )
            .mapValues {
                itemView.findViewById<ImageView>(it.value)
            }



    init {
        itemView.onClick {
            marker?.apply(show)
        }

        itemView
                .findViewById<ImageButton>(R.id.markerDataTrackButton)
                .onClick {
                    marker?.apply(track)
                }
    }

    var marker: DistanceMarker? = null
        set(value) {
            field = value

            value?.apply {
                nameTextView.text = name

                distanceTextView.text = if (distance > 1000) {
                    val distance = (distance/1000.0).roundToLong()
                    "$distance km"
                } else {
                    "${distance.roundToLong()} m"
                }

                genderImage.setImageDrawable(
                        itemView.context.resources.getDrawable(
                                when (gender) {
                                    Gender.Female ->
                                            R.drawable.ic_female
                                    Gender.Male ->
                                            R.drawable.ic_male
                                },
                                null)
                )

                stars.values.forEach {
                    it.setImageDrawable(
                            itemView.context.resources.getDrawable(
                                    android.R.drawable.star_off,
                                    null)
                    )
                }

                stars
                        .filterKeys {
                            it <= skill
                        }
                        .values
                        .forEach {
                            it.setImageDrawable(
                                    itemView.context.resources.getDrawable(
                                            android.R.drawable.star_on,
                                            null)
                            )
                        }

            }
        }
}