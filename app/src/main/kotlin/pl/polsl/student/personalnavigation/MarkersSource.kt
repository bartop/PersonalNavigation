package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox

interface MarkersSource {
    fun getMarkersIn(boundingBox: BoundingBox): Iterable<IdentifiableMarker>
    fun getMarker(id: Long): IdentifiableMarker
}