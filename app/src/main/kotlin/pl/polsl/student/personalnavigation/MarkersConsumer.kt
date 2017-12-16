package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result

interface MarkersConsumer {
    fun consume(markers: Result<Iterable<Marker>, Exception>)
}