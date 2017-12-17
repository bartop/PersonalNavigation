package pl.polsl.student.personalnavigation

import android.content.SharedPreferences
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import java8.util.Optional


class BackendLoginService(
        private val url: String,
        private val preferences: SharedPreferences
): LoginService {
    private val AUTHENTICATION_DATA_KEY = "authentication_data"

    override fun login(): Optional<Exception> {
        try {
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

            return if (response.statusCode !in 200..299) {
                preferences
                        .edit()
                        .remove(AUTHENTICATION_DATA_KEY)
                        .apply()

                Optional.of(InvalidStatusCodeException(
                        response.statusCode,
                        200..299,
                        "$url/authenticate")
                )
            } else {
                Optional.empty<Exception>()
            }

        } catch (e: Exception) {
            return Optional.of(e)
        }
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

