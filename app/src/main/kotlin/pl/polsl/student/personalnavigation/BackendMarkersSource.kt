package pl.polsl.student.personalnavigation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import org.osmdroid.util.BoundingBox

class BackendMarkersSource(private val serverUrl: String) : MarkersSource {

    override fun getMarker(id: Long): Marker {
        return Fuel
                .get(
                        endpointUrl("/marker/$id")
                )
                .responseString()
                .third
                .fold(
                        {
                            jacksonObjectMapper()
                                    .readValue<DefaultMarker>(it)
                        },
                        {
                            throw it
                        }
                )
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Iterable<Marker> {
        return Fuel
                .get(
                        endpointUrl("/markers"),
                        listOf(
                                "boundingBox" to jacksonObjectMapper().writeValueAsString(BackendBoundingBox(boundingBox))
                        )
                )
                .responseString()
                .third
                .fold(
                        {
                            jacksonObjectMapper()
                                    .readValue<Array<DefaultMarker>>(it)
                                    .toList()
                        },
                        {
                            throw it
                        }
                )
    }

    private fun endpointUrl(uri: String): String {
        return "$serverUrl$uri"
    }
}