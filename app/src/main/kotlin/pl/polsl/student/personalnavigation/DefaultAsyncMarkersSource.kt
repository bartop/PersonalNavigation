package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result
import org.osmdroid.util.BoundingBox
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor


class DefaultAsyncMarkersSource(
        private val markersSource: MarkersSource,
        private val threadPoolExecutor: ThreadPoolExecutor
): AsyncMarkersSource {
    override fun getMarkersIn(boundingBox: BoundingBox): Future<Result<Iterable<Marker>, Exception>> {
        return execute { markersSource.getMarkersIn(boundingBox) }
    }

    override fun getMarker(id: Long): Future<Result<Marker, Exception>> {
        return execute { markersSource.getMarker(id) }
    }
    
    private fun <T : Any> execute(task: () -> T): Future<Result<T, Exception>> {
        return threadPoolExecutor.submit(
                Callable {
                    Result.of {
                        task()
                    }
                }
        )
    }
}