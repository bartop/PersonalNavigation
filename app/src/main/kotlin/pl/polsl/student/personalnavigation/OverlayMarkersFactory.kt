package pl.polsl.student.personalnavigation


interface OverlayMarkersFactory {
    fun create(marker: IdentifiableMarker): org.osmdroid.views.overlay.Marker
}