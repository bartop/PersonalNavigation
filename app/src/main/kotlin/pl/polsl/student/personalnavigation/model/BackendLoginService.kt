package pl.polsl.student.personalnavigation.model

import android.content.SharedPreferences
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import pl.polsl.student.personalnavigation.util.InvalidStatusCodeException
import pl.polsl.student.personalnavigation.util.jsonBody
import pl.polsl.student.personalnavigation.util.responseJsonOrThrow


class BackendLoginService(
        private val url: String,
        private val preferences: SharedPreferences
): LoginService {
    private val AUTHENTICATION_DATA_KEY = "authentication_data"

    override fun login(): AuthenticationData {
        val authenticationData = if (preferences.contains(AUTHENTICATION_DATA_KEY)) {
            jacksonObjectMapper().readValue<AuthenticationData>(
                    preferences.getString(AUTHENTICATION_DATA_KEY, "")
            )
        } else {
            register()
        }

        val (request, response) = Fuel
                .post("$url/authenticate")
                .jsonBody(authenticationData)
                .responseString()

        Log.i("BackendLoginService", request.toString())

        with(response.statusCode) {
            if (this !in 200..299) {
                if (this in 400..499) {
                    preferences
                            .edit()
                            .remove(AUTHENTICATION_DATA_KEY)
                            .apply()
                }

                throw InvalidStatusCodeException(
                        response.statusCode,
                        200..299,
                        "$url/authenticate"
                )
            }
        }

        FuelManager.instance.baseHeaders = mapOf(
                "Cookie" to (response.headers["Set-Cookie"]?.joinToString(separator = "; ") ?: "")
        )

        return authenticationData
    }

    private fun register(): AuthenticationData {
        val authenticationData = Fuel
                .post("$url/authentication")
                .responseJsonOrThrow<AuthenticationData>()

        preferences
                .edit()
                .putString(
                        AUTHENTICATION_DATA_KEY,
                        jacksonObjectMapper().writeValueAsString(authenticationData)
                )
                .apply()

        return authenticationData
    }
}
