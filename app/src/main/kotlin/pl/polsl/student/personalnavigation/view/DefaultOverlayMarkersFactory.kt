package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import java8.util.Optional
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.AuthenticationService
import pl.polsl.student.personalnavigation.model.Gender
import pl.polsl.student.personalnavigation.model.IdentifiableMarker
import pl.polsl.student.personalnavigation.model.TrackedMarker


class DefaultOverlayMarkersFactory(
        private val trackedMarker: TrackedMarker,
        private val authenticationService: AuthenticationService
): OverlayMarkersFactory {
    private var bearing = Optional.empty<Float>()

    override fun create(
            mapView: MapView,
            marker: IdentifiableMarker,
            onPressListener: (CustomMarker) -> Unit
    ): Marker {
        val displayableMarker = CustomMarker(
                mapView,
                mapView.context,
                icon(mapView.context, marker),
                marker,
                onPressListener
        )

        bearing.map {
            if (authenticationService.authentication().id == marker.id) {
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

    private fun icon(context: Context, marker: IdentifiableMarker): Drawable {
        val iconId = when {
            authenticationService.authentication().id == marker.id ->
                bearing
                        .map { R.drawable.navigation_arrow }
                        .orElse(R.drawable.marker_green)
            trackedMarker.get().map { marker.id == it }.orElse(false) ->
                    R.drawable.marker_cyan
            marker.gender == Gender.Female ->
                    R.drawable.marker_pink
            else ->
                    R.drawable.marker_blue
        }
        return ResourcesCompat.getDrawable(context.resources, iconId, null)!!
    }
}