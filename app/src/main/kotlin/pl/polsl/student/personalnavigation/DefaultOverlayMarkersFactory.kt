package pl.polsl.student.personalnavigation

import org.osmdroid.views.MapView


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