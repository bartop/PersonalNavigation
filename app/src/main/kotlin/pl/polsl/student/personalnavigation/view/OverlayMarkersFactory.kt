package pl.polsl.student.personalnavigation.view

import pl.polsl.student.personalnavigation.model.IdentifiableMarker


interface OverlayMarkersFactory {
    fun create(marker: IdentifiableMarker): org.osmdroid.views.overlay.Marker
    fun setUserId(userId: Long)
    fun setUserBearing(bearing: Float)
    fun resetUserBearing()
}