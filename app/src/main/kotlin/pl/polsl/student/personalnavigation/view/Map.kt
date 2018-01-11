package pl.polsl.student.personalnavigation.view

import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.model.IdentifiableMarker

/**
 * Created by Bartosz Miera on 2017-12-12.
 */
class Map(
        private val map: MapView,
        private val overlayMarkersFactory: OverlayMarkersFactory
) : MarkersConsumer {

    override fun consume(markers: Iterable<IdentifiableMarker>) {
            map.overlays.clear()
            markers.forEach { marker -> map.overlays.add(overlayMarkersFactory.create(marker)) }
            map.invalidate()
    }
}
