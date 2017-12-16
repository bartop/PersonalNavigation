package pl.polsl.student.personalnavigation

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request

class GetRequestFactory(
        private val path: String,
        private val parameters: List<Pair<String, Any?>> = emptyList()
) : RequestFactory {
    override fun createRequest(): Request = Fuel.get(path, parameters)
}