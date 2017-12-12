package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future

class BackendMarkersSource : MarkersSource {
    override fun toMarkersFuture(boundingBox: BoundingBox): Future<Iterable<MapMarker>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}