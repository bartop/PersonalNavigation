package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future

interface MarkersSource {
    fun getMarkersIn(boundingBox: BoundingBox): Future<Iterable<Marker>>
    fun getMarker(id: Long): Future<Marker>
}