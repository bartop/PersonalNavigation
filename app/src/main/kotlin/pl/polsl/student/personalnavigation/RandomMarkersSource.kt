package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class RandomMarkersSource(
        private val numberOfMarkers: Int = 32
) : MarkersSource {
    private val rng = Random()

    override fun getMarkersIn(boundingBox: BoundingBox): Iterable<Marker> {
        return (1..numberOfMarkers)
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

    override fun getMarker(id: Long): Marker {
        return DefaultMarker(
                    id,
                    id.toString(),
                    Position(
                            latitude = rng.nextDouble() * 180,
                            longitude = rng.nextDouble() * 180,
                            altitude = rng.nextDouble() * 1000
                    )
            )
    }
}