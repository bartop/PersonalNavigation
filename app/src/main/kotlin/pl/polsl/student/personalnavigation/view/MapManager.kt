package pl.polsl.student.personalnavigation.view

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
        private val overlayMarkersFactory: OverlayMarkersFactory
) : MarkersConsumer, MapEventsReceiver {
    private val markersFolder = FolderOverlay()
    private val roadFolder = FolderOverlay()

    init {
        map.overlays.addAll(listOf(roadFolder, markersFolder, MapEventsOverlay(this)))
    }

    override fun consume(markers: Iterable<IdentifiableMarker>) {
        markersFolder.items.forEach { (it as? Marker)?.closeInfoWindow() }
        markersFolder.items.clear()
        markers
                .map(overlayMarkersFactory::create)
                .forEach { markersFolder.add(it) }
        map.invalidate()
    }

    fun showRoad(road: Road) {
        roadFolder.items.clear()
        roadFolder.add(RoadManager.buildRoadOverlay(road))
        map.invalidate()
    }

    fun clearRoad() {
        roadFolder.items.clear()
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
        val closest = markersFolder
                .items
                .mapNotNull { (it as? CustomMarker) }
                .minBy { point.distanceTo(it.position) }

        closest?.onPressListener?.invoke(closest)

        return closest != null
    }
}
