package pl.polsl.student.personalnavigation.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.Fuel
import org.osmdroid.util.BoundingBox
import pl.polsl.student.personalnavigation.util.responseJsonOrThrow

class BackendMarkersSource(private val serverUrl: String) : MarkersSource {

    override fun getMarker(id: Long): IdentifiableMarker {
        return Fuel.get(
                        endpointUrl("/marker/$id")
                )
                .responseJsonOrThrow<DefaultIdentifiableMarker>()
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Set<IdentifiableMarker> {
        return Fuel.get(
                        endpointUrl("/markers"),
                        listOf(
                                "boundingBox" to jacksonObjectMapper().writeValueAsString(BackendBoundingBox(boundingBox))
                        )
                )
                .responseJsonOrThrow<Array<DefaultIdentifiableMarker>>()
                .toSet()
    }

    private fun endpointUrl(uri: String): String {
        return "$serverUrl$uri"
    }
}