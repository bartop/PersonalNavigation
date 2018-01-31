package pl.polsl.student.personalnavigation.model

import java8.util.Optional
import java.util.concurrent.atomic.AtomicReference

class TrackedMarker {
    private val listeners = mutableListOf<(Optional<Long>) -> Unit>()
    private val id = AtomicReference(Optional.empty<Long>())

    fun set(id: Long) {
        this.id.set(Optional.of(id))
        notifyListeners()
    }

    fun reset() {
        id.set(Optional.empty())
        notifyListeners()
    }

    fun get(): Optional<Long> {
        return id.get()
    }

    fun addListener(callback: (Optional<Long>) -> Unit) {
        listeners.add(callback)
    }

    private fun notifyListeners() {
        listeners.forEach { it(get()) }
    }
}