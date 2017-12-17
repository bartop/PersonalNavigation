package pl.polsl.student.personalnavigation

import java8.util.Optional
import java8.util.concurrent.CompletableFuture
import java8.util.function.Supplier
import java.util.concurrent.Executor


class AsyncLoginService(
        private val loginService: LoginService,
        private val executor: Executor
) {
    fun login(): CompletableFuture<Optional<Exception>> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    loginService.login()
                },
                executor
        )
    }
}