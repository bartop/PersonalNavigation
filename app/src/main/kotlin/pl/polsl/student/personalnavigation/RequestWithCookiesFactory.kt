package pl.polsl.student.personalnavigation

import com.github.kittinunf.fuel.core.Request
import kotlin.collections.Map

class RequestWithCookiesFactory(val requestFactory: RequestFactory, val cookies: Map<String, String> ) : RequestFactory {
    override fun createRequest(): Request {
        val request = requestFactory.createRequest()
        val cookiesString = cookies.map{ (key, value) ->
            "$key=$value"
        }.joinToString(separator = ";")
        request.headers.put("cookie", cookiesString)
        return request
    }
}