package pl.polsl.student.personalnavigation.view

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

    init {
        map.overlays.add(markersFolder)
    }

    override fun consume(markers: Iterable<IdentifiableMarker>) {
        markersFolder.items.clear()
        markers
                .map(overlayMarkersFactory::create)
                .forEach { markersFolder.add(it) }
        map.invalidate()
    }
}
