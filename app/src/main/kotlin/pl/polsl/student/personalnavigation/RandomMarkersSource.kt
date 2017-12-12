package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class RandomMarkersSource(private val map: MapView) : MarkersSource {
    override fun toMarkersFuture(boundingBox: BoundingBox): Future<Iterable<MapMarker>> {
        val rng = Random()
        return FutureTask(
                { arrayListOf(BasicMapMarker(map, GeoPoint(rng.nextDouble(), rng.nextDouble()))) }
        )
    }
}