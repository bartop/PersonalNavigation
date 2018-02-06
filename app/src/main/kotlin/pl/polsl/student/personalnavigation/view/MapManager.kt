package pl.polsl.student.personalnavigation.view

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import pl.polsl.student.personalnavigation.model.IdentifiableMarker

class MapManager(
        private val map: MapView,
        private val overlayMarkersFactory: OverlayMarkersFactory,
        private val onPressListener: (CustomMarker) -> Unit
) : MarkersConsumer {
    private val markersFolder = FolderOverlay()
    private val roadFolder = FolderOverlay()
    private var clusterer = CustomMarkerClusterer(map.context)

    init {
        map.overlays.addAll(listOf(roadFolder, markersFolder))
    }

    override fun consume(markers: Iterable<IdentifiableMarker>) {
        try {
            clusterer.items.forEach { it.closeInfoWindow() }
            markersFolder.items.clear()
            clusterer = CustomMarkerClusterer(map.context)
            markers
                    .map { overlayMarkersFactory.create(map, it, onPressListener) }
                    .forEach { clusterer.add(it) }
            markersFolder.add(clusterer)
        } finally {
            map.invalidate()
        }
    }

    fun showRoad(road: Road) {
        roadFolder.items.clear()
        roadFolder.add(RoadManager.buildRoadOverlay(road))
        map.invalidate()
    }

    fun clearRoad() {
        roadFolder.items.clear()
        map.invalidate()
    }
}
