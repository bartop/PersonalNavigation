package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future

class BackendMarkersSource : MarkersSource {
    override fun getMarker(id: Long): Future<Marker> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Future<Iterable<Marker>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}