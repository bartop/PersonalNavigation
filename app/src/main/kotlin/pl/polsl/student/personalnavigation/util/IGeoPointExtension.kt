package pl.polsl.student.personalnavigation.util

import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint


fun IGeoPoint.toGeoPoint(): GeoPoint = GeoPoint(this.latitude, this.longitude)
fun IGeoPoint.hasSameLatitudeLongitude(other: IGeoPoint) =
        this.latitude == other.latitude && this.longitude == other.longitude