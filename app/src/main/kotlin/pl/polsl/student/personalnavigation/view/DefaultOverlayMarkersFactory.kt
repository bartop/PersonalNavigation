package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import java8.util.Optional
import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.AuthenticationService
import pl.polsl.student.personalnavigation.model.IdentifiableMarker
import pl.polsl.student.personalnavigation.model.TrackedMarker


class DefaultOverlayMarkersFactory(
        private val context: Context,
        private val mapView: MapView,
        private val trackedMarker: TrackedMarker,
        private val onLongPressListener: (CustomMarker) -> Unit
): OverlayMarkersFactory {
    private var userId: Optional<Long> = Optional.empty()
    private var bearing = Optional.empty<Float>()

    override fun setUserId(userId: Long) {
        this.userId = Optional.of(userId)
    }

    override fun create(marker: IdentifiableMarker): org.osmdroid.views.overlay.Marker {
        val displayableMarker = CustomMarker(
                mapView,
                context,
                icon(marker),
                marker,
                onLongPressListener
        )

        bearing.map {
            if (userId.map { id -> marker.id == id }.orElse(false)) {
                displayableMarker.rotation = -45.0f + it
            }
        }

        return displayableMarker
    }

    override fun setUserBearing(bearing: Float) {
        this.bearing = Optional.of(bearing)
    }

    override fun resetUserBearing() {
        this.bearing = Optional.empty()
    }

    private fun icon(marker: IdentifiableMarker): Drawable {
        val iconId = when {
            userId.map { marker.id == it }.orElse(false) ->
                bearing
                        .map { R.drawable.navigation_arrow }
                        .orElse(R.drawable.marker_green)
            trackedMarker.get().map { marker.id == it }.orElse(false) ->
                    R.drawable.marker_cyan
            else ->
                    R.drawable.marker_blue
        }
        return ResourcesCompat.getDrawable(context.resources, iconId, null)!!
    }
}