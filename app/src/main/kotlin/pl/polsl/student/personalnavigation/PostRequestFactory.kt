package pl.polsl.student.personalnavigation

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request

class PostRequestFactory(
        private val path: String,
        private val body: ByteArray,
        private val parameters: List<Pair<String, Any?>>
) : RequestFactory {
    override fun createRequest(): Request {
        val request = Fuel.post(path, parameters)
        request.body(body)
        return request
    }
}