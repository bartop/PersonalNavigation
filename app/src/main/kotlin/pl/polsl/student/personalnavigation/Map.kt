package pl.polsl.student.personalnavigation

import org.osmdroid.views.MapView
import java.util.concurrent.Future

/**
 * Created by Bartosz Miera on 2017-12-12.
 */
class Map(private val map: MapView, private val uiThreadExecutor: (() ->  Unit) -> Unit) : AsynchronousMarkersConsumer {
    private var mapMarkers: Future<Iterable<MapMarker>>? = null

    override fun consume(markers: Future<Iterable<MapMarker>>) {
        if (mapMarkers != null) mapMarkers!!.cancel(true)
        mapMarkers = markers
        Thread({
            updateMap(markers)
        }).start()
    }

    private fun updateMap(markers: Future<Iterable<MapMarker>>) {
        synchronized(this, {
            val overlays = markers.get().map { mapMarker -> mapMarker.toOsmOverlay() }
            uiThreadExecutor({
                map.overlays.clear()
                overlays.forEach { overlay -> map.overlays.add(overlay) }
                map.invalidate()
            })
        })
    }
}
