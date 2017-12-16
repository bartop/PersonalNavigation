package pl.polsl.student.personalnavigation

import com.github.kittinunf.fuel.core.Request

interface RequestFactory {
    fun createRequest() : Request
}