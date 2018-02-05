package pl.polsl.student.personalnavigation.model

import android.location.Location

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import pl.polsl.student.personalnavigation.util.FutureResult
import pl.polsl.student.personalnavigation.util.jsonBody
import java8.util.concurrent.CompletableFuture
import java8.util.function.Supplier
import java.util.concurrent.Executor

class LocationSender(
        private val url: String,
        private val authenticationService: AuthenticationService,
        private val executor: Executor
): AnkoLogger {
    fun postLocation(marker: Marker): FutureResult<Unit> {
        return CompletableFuture.supplyAsync(
                Supplier<Result<Unit, Exception>> { postLocationSynchronous(marker) },
                executor
        )
    }

    private fun postLocationSynchronous(marker: Marker): Result<Unit, Exception> {
        return Result.of {
            Fuel
                    .post("$url/markers")
                    .jsonBody(
                            marker
                    )
                    .header(authenticationService.authenticationHeaders())
                    .responseString { request, response, _ ->
                        if (response.statusCode !in 200..299) {
                            warn("Request failed:\n$request")
                            warn("$response")

                            if (response.statusCode in 400..499) {
                                authenticationService.invalidateAuthentication()
                            }
                        }
                    }
            Unit
        }
    }
}