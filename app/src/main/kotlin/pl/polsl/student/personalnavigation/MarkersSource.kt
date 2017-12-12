package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future

interface MarkersSource {
    fun toMarkersFuture(boundingBox: BoundingBox): Future<Iterable<MapMarker>>
}