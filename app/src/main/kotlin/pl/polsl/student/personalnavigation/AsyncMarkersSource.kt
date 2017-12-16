package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result
import org.osmdroid.util.BoundingBox
import java.util.concurrent.Future


interface AsyncMarkersSource {
    fun getMarkersIn(boundingBox: BoundingBox): Future<Result<Iterable<Marker>, Exception>>
    fun getMarker(id: Long): Future<Result<Marker, Exception>>
}