package pl.polsl.student.personalnavigation


interface OverlayMarkersFactory {
    fun create(marker: Marker): org.osmdroid.views.overlay.Marker
}