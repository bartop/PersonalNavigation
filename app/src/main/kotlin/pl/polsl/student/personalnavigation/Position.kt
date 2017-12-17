package pl.polsl.student.personalnavigation

import android.location.Location
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

    companion object {
        fun fromLocation(location: Location): Position {
            return Position(
                    longitude = location.longitude,
                    latitude = location.latitude,
                    altitude = location.altitude
            )
        }
    }
}
