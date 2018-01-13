package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import java8.util.Optional
import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.AuthenticationService
import pl.polsl.student.personalnavigation.model.IdentifiableMarker


class DefaultOverlayMarkersFactory(
        private val context: Context,
        private val mapView: MapView,
        private val onLongPressListener: (CustomMarker) -> Unit
): OverlayMarkersFactory {
    private var userId: Optional<Long> = Optional.empty()
    private var trackedId: Optional<Long> = Optional.empty()

    override fun setUserId(userId: Long) {
        this.userId = Optional.of(userId)
    }

    override fun setTrackedId(trackedId: Long) {
        this.trackedId = Optional.of(trackedId)
    }

    override fun create(marker: IdentifiableMarker): org.osmdroid.views.overlay.Marker {
        val displayableMarker = CustomMarker(
                mapView,
                context,
                icon(marker),
                marker,
                onLongPressListener
        )

        return displayableMarker
    }

    private fun icon(marker: IdentifiableMarker): Drawable {
        val iconId = when {
            userId.map { marker.id == it }.orElse(false) ->
                    R.drawable.marker_green
            trackedId.map { marker.id == it }.orElse(false) ->
                    R.drawable.marker_blue
            else ->
                    R.drawable.marker_black
        }
        return ResourcesCompat.getDrawable(context.resources, iconId, null)!!
    }
}