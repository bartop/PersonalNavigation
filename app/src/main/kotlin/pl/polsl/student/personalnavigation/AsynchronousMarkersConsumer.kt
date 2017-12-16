package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result
import java.util.concurrent.Future

interface AsynchronousMarkersConsumer {
    fun consume(marker: Future<Result<Iterable<Marker>, Exception>>)
}