package pl.polsl.student.personalnavigation.view

import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.model.IdentifiableMarker


class DefaultOverlayMarkersFactory(
        private val mapView: MapView
): OverlayMarkersFactory {
    override fun create(marker: IdentifiableMarker): org.osmdroid.views.overlay.Marker {
        val displayableMarker = org.osmdroid.views.overlay.Marker(mapView)
        displayableMarker.position  = marker.position.toGeoPoint()
        displayableMarker.title = marker.name
        return displayableMarker
    }
}