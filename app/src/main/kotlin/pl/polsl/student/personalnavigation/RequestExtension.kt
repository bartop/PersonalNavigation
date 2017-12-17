package pl.polsl.student.personalnavigation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.Request


inline fun <reified T> Request.responseJsonOrThrow(): T {
    val jsonString = this
            .responseString()
            .third
            .get()

    return jacksonObjectMapper().readValue(jsonString, T::class.java)
}

inline fun <reified T> Request.jsonBody(toJson: T): Request {
    return this.body(jacksonObjectMapper().writeValueAsString(toJson))
            .header("Content-Type" to "application/json; charset=utf-8")
}