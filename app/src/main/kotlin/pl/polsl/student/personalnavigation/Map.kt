package pl.polsl.student.personalnavigation

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
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
    private var markers: Future<Iterable<Marker>>? = null

    override fun consume(markers: Future<Iterable<Marker>>) {
        this.markers?.cancel(true)
        this.markers = markers
        Thread{
            updateMap(markers)
        }.start()
    }

    private fun updateMap(markersFuture: Future<Iterable<Marker>>) {
        synchronized(this) {
            try {
                val markers = markersFuture.get()
                uiThreadExecutor(Runnable{
                    map.overlays.clear()
                    markers.forEach { marker -> map.overlays.add(createDisplayableMarker(map, marker)) }
                    map.invalidate()
                })
            } catch(interruptedException: InterruptedException) {
                Logger.getAnonymousLogger().log(Level.WARNING, interruptedException.message)
                return
            }
        }
    }

    private fun createDisplayableMarker(
            mapView: MapView,
            marker: Marker
    ): Overlay {
        val displayableMarker = org.osmdroid.views.overlay.Marker(mapView)
        displayableMarker.position  = marker.position.toGeoPoint()
        displayableMarker.title = marker.name
        return displayableMarker
    }
}
