package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor

class BackendMarkersSource(private val serverUrl: String) : MarkersSource {

    override fun getMarker(id: Long): Marker {
        TODO()
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Iterable<Marker> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}