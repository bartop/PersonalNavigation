package pl.polsl.student.personalnavigation.model

import android.content.SharedPreferences
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import java8.util.Optional
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.polsl.student.personalnavigation.util.InvalidStatusCodeException
import pl.polsl.student.personalnavigation.util.jsonBody
import pl.polsl.student.personalnavigation.util.responseJsonOrThrow


class BackendAuthenticationService(
        private val url: String,
        private val preferences: SharedPreferences
): AuthenticationService, AnkoLogger {
    private val AUTHENTICATION_DATA_KEY = "authentication_data"

    private var authenticationDataCache: Optional<AuthenticationData> = Optional.empty()
    private var authenticationHeadersCache: Optional<Map<String, String>> = Optional.empty()


    override fun authenticationHeaders(): Map<String, String> {
        synchronized(this) {
            if (!authenticationHeadersCache.isPresent) {
                val (authenticationData, headers) = login()
                authenticationDataCache = Optional.of(authenticationData)
                authenticationHeadersCache = Optional.of(headers)
            }

            return authenticationHeadersCache.get()
        }
    }

    override fun authentication(): AuthenticationData {
        synchronized(this) {
            if (!authenticationDataCache.isPresent) {
                val (authenticationData, headers) = login()
                authenticationDataCache = Optional.of(authenticationData)
                authenticationHeadersCache = Optional.of(headers)
            }

            return authenticationDataCache.get()
        }
    }

    override fun invalidateAuthentication() {
        synchronized(this) {
            authenticationDataCache = Optional.empty()
            authenticationHeadersCache = Optional.empty()
        }
    }

    private fun login(): Pair<AuthenticationData, Map<String, String>> {
        val authenticationData = if (preferences.contains(AUTHENTICATION_DATA_KEY)) {
            jacksonObjectMapper().readValue<AuthenticationData>(
                    preferences.getString(AUTHENTICATION_DATA_KEY, "")
            )
        } else {
            register()
        }

        val (_, response) = Fuel
                .post("$url/authenticate")
                .jsonBody(authenticationData)
                .responseString()

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

        val headers = mapOf(
                "Cookie" to (response.headers["Set-Cookie"]?.joinToString(separator = "; ") ?: "")
        )

        return authenticationData to headers
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

