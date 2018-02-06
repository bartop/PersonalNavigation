package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.drawable.Drawable
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

    override fun create(
            mapView: MapView,
            marker: IdentifiableMarker,
            onPressListener: (CustomMarker) -> Unit
    ): Marker {
        return CustomMarker(
                mapView,
                mapView.context,
                icon(mapView.context, marker),
                marker,
                onPressListener
        )
    }

    private fun icon(context: Context, marker: IdentifiableMarker): Drawable {
        val iconId = when {
            authenticationService.authentication().id == marker.id ->
                R.drawable.marker_green
            trackedMarker.get().map { it == marker.id }.orElse(false) ->
                    R.drawable.marker_cyan
            marker.gender == Gender.Female ->
                    R.drawable.marker_pink
            else ->
                    R.drawable.marker_blue
        }

        return context.resources.getDrawable(iconId, null)!!
    }
}