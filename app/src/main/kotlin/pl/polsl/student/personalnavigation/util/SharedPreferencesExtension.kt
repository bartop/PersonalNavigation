package pl.polsl.student.personalnavigation.util

import android.content.SharedPreferences
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

inline fun <reified T> SharedPreferences.getJson(key: String): T {
    val mapped = this.getString(key, "")
    return jacksonObjectMapper().readValue<T>(mapped, T::class.java)
}

fun SharedPreferences.Editor.putJson(key: String, obj: Any) =
        this.putString(
                key,
                jacksonObjectMapper().writeValueAsString(obj)
        )