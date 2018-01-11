package pl.polsl.student.personalnavigation.model

import pl.polsl.student.personalnavigation.model.Position


interface Marker {
    val name: String
    val position: Position
}