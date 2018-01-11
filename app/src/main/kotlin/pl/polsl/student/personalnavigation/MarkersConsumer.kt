package pl.polsl.student.personalnavigation

import com.github.kittinunf.result.Result

interface MarkersConsumer {
    fun consume(markers: Iterable<IdentifiableMarker>)
}