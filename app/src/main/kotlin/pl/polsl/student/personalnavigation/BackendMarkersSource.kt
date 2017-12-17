package pl.polsl.student.personalnavigation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.osmdroid.util.BoundingBox

class BackendMarkersSource(private val serverUrl: String) : MarkersSource {

    override fun getMarker(id: Long): IdentifiableMarker {
        return GetRequestFactory(
                        endpointUrl("/marker/$id")
                )
                .createRequest()
                .responseJsonOrThrow<DefaultIdentifiableMarker>()
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Iterable<IdentifiableMarker> {
        return GetRequestFactory(
                        endpointUrl("/markers"),
                        listOf(
                                "boundingBox" to jacksonObjectMapper().writeValueAsString(BackendBoundingBox(boundingBox))
                        )
                )
                .createRequest()
                .responseJsonOrThrow<Array<DefaultIdentifiableMarker>>()
                .toList()
    }

    private fun endpointUrl(uri: String): String {
        return "$serverUrl$uri"
    }
}