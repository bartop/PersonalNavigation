package pl.polsl.student.personalnavigation

import com.github.kittinunf.fuel.core.Request
import kotlin.collections.Map

class RequestWithCookiesFactory(val requestFactory: RequestFactory, val cookies: Map<String, String> ) : RequestFactory {
    override fun createRequest(): Request {
        val request = requestFactory.createRequest()
        request.headers.putAll(cookies)
        return request
    }
}