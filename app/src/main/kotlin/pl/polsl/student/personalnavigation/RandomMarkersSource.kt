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

    override fun getMarkersIn(boundingBox: BoundingBox): Future<Iterable<Marker>> {
        val task = FutureTask<Iterable<Marker>>(
                {
                    (1..numberOfMarkers)
                            .map {
                                DefaultMarker(
                                        it.toLong(),
                                        it.toString(),
                                        Position(
                                                latitude = rng.nextDouble() * boundingBox.latitudeSpan + boundingBox.latSouth,
                                                longitude = rng.nextDouble() * boundingBox.longitudeSpan + boundingBox.lonWest,
                                                altitude = rng.nextDouble() * 1000
                                        )

                                )
                            }
                            .toList()
                }
        )
        task.run()
        return task
    }

    override fun getMarker(id: Long): Future<Marker> {
        val task = FutureTask<Marker>(
                {

                    DefaultMarker(
                                id,
                                id.toString(),
                                Position(
                                        latitude = rng.nextDouble() * 180,
                                        longitude = rng.nextDouble() * 180,
                                        altitude = rng.nextDouble() * 1000
                                )
                        )
                }
        )
        task.run()
        return task
    }
}