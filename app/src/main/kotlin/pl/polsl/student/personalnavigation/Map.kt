package pl.polsl.student.personalnavigation

import org.osmdroid.views.MapView
import java.util.concurrent.Future
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by Bartosz Miera on 2017-12-12.
 */
class Map(
        private val map: MapView,
        private val uiThreadExecutor: (Runnable) -> Unit
) : AsynchronousMarkersConsumer {
    private var mapMarkers: Future<Iterable<MapMarker>>? = null

    override fun consume(markers: Future<Iterable<MapMarker>>) {
        mapMarkers?.cancel(true)
        mapMarkers = markers
        Thread{
            updateMap(markers)
        }.start()
    }

    private fun updateMap(markersFuture: Future<Iterable<MapMarker>>) {
        synchronized(this) {
            try {
                val markers = markersFuture.get()
                uiThreadExecutor(Runnable{
                    map.overlays.clear()
                    markers.forEach { marker -> map.overlays.add(marker.toOsmOverlay()) }
                    map.invalidate()
                })
            } catch(interruptedException: InterruptedException) {
                Logger.getAnonymousLogger().log(Level.WARNING, interruptedException.message)
                return
            }
        }
    }
}
