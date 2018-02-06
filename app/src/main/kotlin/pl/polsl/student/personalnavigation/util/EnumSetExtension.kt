package pl.polsl.student.personalnavigation.util

import java.util.*

inline fun <reified E: Enum<E>> Collection<E>.toEnumSet() : EnumSet<E> {
    return if (this.isEmpty()) {
        EnumSet.noneOf(E::class.java)
    } else {
        EnumSet.copyOf(this)
    }
}