package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class RandomMarkersSource(
        private val map: MapView,
        private val numberOfMarkers: Int = 32
) : MarkersSource {
    private val rng = Random()

    override fun toMarkersFuture(boundingBox: BoundingBox): Future<Iterable<MapMarker>> {
        val task = FutureTask<Iterable<MapMarker>>(
                {
                    (1..numberOfMarkers)
                            .map {
                                GeoPoint(
                                        rng.nextDouble() * boundingBox.latitudeSpan + boundingBox.latSouth,
                                        rng.nextDouble() * boundingBox.longitudeSpan + boundingBox.lonWest
                                )
                            }
                            .map {
                                BasicMapMarker(map, it)
                            }
                            .toList()
                }
        )
        task.run()
        return task
    }
}