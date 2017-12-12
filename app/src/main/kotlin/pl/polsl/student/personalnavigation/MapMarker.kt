package pl.polsl.student.personalnavigation

import org.osmdroid.views.overlay.Overlay

interface MapMarker {
    fun toOsmOverlay(): Overlay
}