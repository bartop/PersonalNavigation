package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future

interface MarkersSource {
    fun getMarkersIn(boundingBox: BoundingBox): Iterable<Marker>
    fun getMarker(id: Long): Marker
}