package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.*

class RandomMarkersSource(
        private val numberOfMarkers: Int = 32
) : MarkersSource {
    private val rng = Random()

    override fun getMarkersIn(boundingBox: BoundingBox): Set<IdentifiableMarker> {
        return (1..numberOfMarkers)
                    .map {
                        DefaultIdentifiableMarker(
                                it.toLong(),
                                it.toString(),
                                Position(
                                        latitude = rng.nextDouble() * boundingBox.latitudeSpan + boundingBox.latSouth,
                                        longitude = rng.nextDouble() * boundingBox.longitudeSpan + boundingBox.lonWest,
                                        altitude = rng.nextDouble() * 1000
                                )

                        )
                    }
                    .toSet()

    }

    override fun getMarker(id: Long): IdentifiableMarker {
        return DefaultIdentifiableMarker(
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