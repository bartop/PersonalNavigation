package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox


class BackendBoundingBox(osmBoundingBox: BoundingBox) {
    val north = osmBoundingBox.latNorth
    val south = osmBoundingBox.latSouth
    val east = osmBoundingBox.lonEast
    val west = osmBoundingBox.lonWest
}