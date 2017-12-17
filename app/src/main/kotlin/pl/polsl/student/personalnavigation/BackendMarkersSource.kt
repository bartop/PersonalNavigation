package pl.polsl.student.personalnavigation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import org.osmdroid.util.BoundingBox

class BackendMarkersSource(private val serverUrl: String) : MarkersSource {

    override fun getMarker(id: Long): Marker {
        return GetRequestFactory(
                        endpointUrl("/marker/$id")
                )
                .createRequest()
                .responseJsonOrThrow<DefaultMarker>()
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Iterable<Marker> {
        return GetRequestFactory(
                        endpointUrl("/markers"),
                        listOf(
                                "boundingBox" to jacksonObjectMapper().writeValueAsString(BackendBoundingBox(boundingBox))
                        )
                )
                .createRequest()
                .responseJsonOrThrow<Array<DefaultMarker>>()
                .toList()
    }

    private fun endpointUrl(uri: String): String {
        return "$serverUrl$uri"
    }
}