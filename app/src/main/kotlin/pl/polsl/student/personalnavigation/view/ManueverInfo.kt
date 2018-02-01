package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import org.osmdroid.bonuspack.routing.Road
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.util.totalLengthDurationText
import kotlin.math.roundToLong


class ManeuverInfo(private val context: Context, private val road: Road) {
    val currentNode = with (road.mNodes) {
        getOrElse(1) { first() }
    }

    fun icon(): Drawable {
        val iconMap = mapOf(
                0..0 to R.drawable.ic_empty,
                1..1 to R.drawable.ic_continue,
                2..2 to  R.drawable.ic_empty,
                3..3 to R.drawable.ic_turn_slight_left,
                4..4 to R.drawable.ic_turn_left,
                5..5 to R.drawable.ic_turn_sharp_left,
                6..6 to R.drawable.ic_turn_slight_right,
                7..7 to R.drawable.ic_turn_right,
                8..8 to R.drawable.ic_turn_sharp_right,
                9..11 to R.drawable.ic_continue,
                12..13 to R.drawable.ic_u_turn_left,
                14..14 to R.drawable.ic_u_turn_right,
                24..26 to R.drawable.ic_arrived
        )

        val resId = iconMap
                .filterKeys { currentNode.mManeuverType in it }
                .values
                .firstOrNull() ?: R.drawable.ic_empty

        return ResourcesCompat.getDrawable(context.resources, resId, null)!!
    }

    fun instructions(): String {
        return currentNode.mInstructions
    }

    fun totalLengthDurationText(): String {
        return road.totalLengthDurationText(context)
    }

    fun distanceText(): String {
        val distance = road.mNodes.firstOrNull()?.mLength

        return if (distance != null) {
            "${(distance * 1000).roundToLong()}m"
        } else {
            ""
        }
    }
}