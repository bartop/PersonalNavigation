package pl.polsl.student.personalnavigation.view

import pl.polsl.student.personalnavigation.model.IdentifiableMarker

interface MarkersConsumer {
    fun consume(markers: Iterable<IdentifiableMarker>)
}