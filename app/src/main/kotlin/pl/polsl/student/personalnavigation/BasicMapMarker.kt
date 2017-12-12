package pl.polsl.student.personalnavigation

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

class BasicMapMarker(val map: org.osmdroid.views.MapView, val geoPoint: GeoPoint) : MapMarker {
    override fun toOsmOverlay(): Overlay {
        val marker = Marker(map)
        marker.position = geoPoint
        return marker
    }
}