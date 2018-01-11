package pl.polsl.student.personalnavigation.model

import com.github.kittinunf.result.Result
import java8.util.concurrent.CompletableFuture
import java8.util.function.Supplier
import java.util.concurrent.Executor


class AsyncLoginService(
        private val loginService: LoginService,
        private val executor: Executor
) {
    fun login(): CompletableFuture<Result<AuthenticationData, Exception>> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    Result.of {
                        loginService.login()
                    }
                },
                executor
        )
    }
}