package pl.polsl.student.personalnavigation.view

import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import pl.polsl.student.personalnavigation.model.IdentifiableMarker

/**
 * Created by Bartosz Miera on 2017-12-12.
 */
class Map(
        private val map: MapView,
        private val overlayMarkersFactory: OverlayMarkersFactory
) : MarkersConsumer {
    private val markersFolder = FolderOverlay()
    private val roadFolder = FolderOverlay()

    init {
        map.overlays.addAll(listOf(markersFolder, roadFolder))
    }

    override fun consume(markers: Iterable<IdentifiableMarker>) {
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
}
