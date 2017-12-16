package pl.polsl.student.personalnavigation

import android.util.Log
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by Bartosz Miera on 2017-12-12.
 */
class Map(
        private val map: MapView,
        private val threadPoolExecutor: ThreadPoolExecutor,
        private val uiThreadExecutor: (Runnable) -> Unit
) : AsynchronousMarkersConsumer {
    private var markers: Future<Result<Iterable<Marker>, Exception>>? = null

    override fun consume(markers: Future<Result<Iterable<Marker>, Exception>>) {
        this.markers?.cancel(true)
        this.markers = markers
        threadPoolExecutor.execute{
            updateMap(markers)
        }
    }

    private fun updateMap(markersFuture: Future<Result<Iterable<Marker>, Exception>>) {
        synchronized(this) {
            try {
                //TODO: error handling and probably refactoring
                val markers = markersFuture.get().fold(
                        {
                            uiThreadExecutor(Runnable {
                                map.overlays.clear()
                                it.forEach { marker -> map.overlays.add(createDisplayableMarker(map, marker)) }
                                map.invalidate()
                            })
                        },
                        {
                            Log.e("Map", "Cannot update map", it)
                        }
                )
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
