package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result
import org.osmdroid.util.BoundingBox
import java8.util.concurrent.CompletableFuture


interface AsyncMarkersSource {
    fun getMarkersIn(boundingBox: BoundingBox): CompletableFuture<Result<Iterable<IdentifiableMarker>, Exception>>
    fun getMarker(id: Long): CompletableFuture<Result<IdentifiableMarker, Exception>>
}