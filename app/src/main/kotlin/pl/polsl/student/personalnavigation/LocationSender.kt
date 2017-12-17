package pl.polsl.student.personalnavigation

import android.location.Location
import android.util.Log
import com.github.kittinunf.fuel.Fuel


class LocationSender(
        private val url: String,
        private val nameGetter: () -> String
) {
    fun postLocation(location: Location) {
        Fuel
                .post("$url/markers")
                .jsonBody(
                        DefaultMarker(
                                name = nameGetter(),
                                position = Position.fromLocation(location)
                        )
                )
                .response { request, response, _ ->
                    if (response.statusCode !in 200..299) {
                        Log.e(this.javaClass.simpleName, "Request:\n$request")
                        Log.e(this.javaClass.simpleName, "Response:\n$response")
                    }
                }
    }
}