package pl.polsl.student.personalnavigation

import org.osmdroid.util.GeoPoint

/**
 * Created by Bartosz Miera on 2017-12-15.
 */
class Position(
        val longitude: Double,
        val latitude: Double,
        val altitude: Double
) {
    fun toGeoPoint() = GeoPoint(latitude, longitude, altitude)
}
