package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result
import java8.util.concurrent.CompletableFuture
import java8.util.function.Supplier
import org.osmdroid.util.BoundingBox
import java.util.concurrent.Executor

class DefaultAsyncMarkersSource(
        private val markersSource: MarkersSource,
        private val executor: Executor
): AsyncMarkersSource {
    override fun getMarkersIn(boundingBox: BoundingBox): CompletableFuture<Result<Iterable<Marker>, Exception>> {
        return execute { markersSource.getMarkersIn(boundingBox) }
    }

    override fun getMarker(id: Long): CompletableFuture<Result<Marker, Exception>> {
        return execute { markersSource.getMarker(id) }
    }

    private fun <T : Any> execute(task: () -> T): CompletableFuture<Result<T, Exception>> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    Result.of {
                        task()
                    }
                },
                executor
        )
    }
}