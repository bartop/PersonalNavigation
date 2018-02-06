package pl.polsl.student.personalnavigation.model

import org.osmdroid.util.BoundingBox
import pl.polsl.student.personalnavigation.model.IdentifiableMarker

interface MarkersSource {
    fun getMarkersIn(boundingBox: BoundingBox): Set<IdentifiableMarker>
    fun getMarker(id: Long): IdentifiableMarker
    fun getFilteredMarkers(limit: Int): List<DistanceMarker>
}