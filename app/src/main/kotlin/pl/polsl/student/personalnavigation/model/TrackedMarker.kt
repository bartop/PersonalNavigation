package pl.polsl.student.personalnavigation.model

import java8.util.Optional
import java.util.concurrent.atomic.AtomicReference

class TrackedMarker {
    private val id = AtomicReference(Optional.empty<Long>())

    fun set(id: Long) {
        this.id.set(Optional.of(id))
    }

    fun reset() {
        id.set(Optional.empty())
    }

    fun get(): Optional<Long> {
        return id.get()
    }
}