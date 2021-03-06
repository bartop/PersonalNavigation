package pl.polsl.student.personalnavigation.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.Fuel
import org.osmdroid.util.BoundingBox
import pl.polsl.student.personalnavigation.util.responseJsonOrThrow

class BackendMarkersSource(
        private val serverUrl: String,
        private val authenticationService: AuthenticationService,
        private val filterDataRepository: FilterDataRepository
) : MarkersSource {

    override fun getMarker(id: Long): IdentifiableMarker {
        return Fuel.get(
                        endpointUrl("/marker/$id")
                )
                .responseJsonOrThrow<DefaultIdentifiableMarker>()
    }

    override fun getMarkersIn(boundingBox: BoundingBox): Set<IdentifiableMarker> {
        return Fuel
                .get(
                        endpointUrl("/markers"),
                        listOf(
                                "boundingBox" to jacksonObjectMapper().
                                        writeValueAsString(BackendBoundingBox(boundingBox)),
                                "genders" to filterDataRepository.get().genders.joinToString(),
                                "skills" to filterDataRepository.get().skills.joinToString()
                        )
                )
                .header(authenticationService.authenticationHeaders())
                .responseJsonOrThrow<Array<DefaultIdentifiableMarker>>()
                .toSet()
    }

    override fun getFilteredMarkers(limit: Int): List<DistanceMarker> {
        return Fuel
                .get(
                    endpointUrl("/markers/sorted"),
                    listOf(
                            "limit" to limit.toString(),
                            "genders" to filterDataRepository.get().genders.joinToString(),
                            "skills" to filterDataRepository.get().skills.joinToString()
                    )
                )
                .header(authenticationService.authenticationHeaders())
                .responseJsonOrThrow<Array<DistanceMarker>>()
                .toList()
    }

    private fun endpointUrl(uri: String): String {
        return "$serverUrl$uri"
    }
}