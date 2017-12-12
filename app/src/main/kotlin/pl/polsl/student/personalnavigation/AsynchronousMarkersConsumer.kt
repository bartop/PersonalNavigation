package pl.polsl.student.personalnavigation

import java.util.concurrent.Future

interface AsynchronousMarkersConsumer {
    fun consume(marker: Future<Iterable<MapMarker>>)
}