package pl.polsl.student.personalnavigation.view

import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.model.IdentifiableMarker


interface OverlayMarkersFactory {
    fun create(
            mapView: MapView,
            marker: IdentifiableMarker,
            onPressListener: (CustomMarker) -> Unit
    ): org.osmdroid.views.overlay.Marker

    fun setUserBearing(bearing: Float)
    fun resetUserBearing()
}