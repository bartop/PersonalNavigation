package pl.polsl.student.personalnavigation.util

import com.github.kittinunf.result.Result
import java8.util.concurrent.CompletableFuture

typealias FutureResult<T> = CompletableFuture<Result<T, Exception>>