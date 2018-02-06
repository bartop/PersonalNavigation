package pl.polsl.student.personalnavigation.model

import java8.util.Optional
import java.util.concurrent.atomic.AtomicReference

class TrackedMarker {
    private val listeners = mutableListOf<(Optional<Long>) -> Unit>()
    private var id = Optional.empty<Long>()

    fun set(id: Long) {
        synchronized(this) {
            this.id = Optional.of(id)
            notifyListeners()
        }
    }

    fun reset() {
        synchronized(this) {
            id = Optional.empty()
            notifyListeners()
        }
    }

    fun get(): Optional<Long> {
        synchronized(this) {
            return id
        }
    }

    fun addListener(callback: (Optional<Long>) -> Unit) {
        synchronized(this) {
            listeners.add(callback)
        }
    }

    private fun notifyListeners() {
        listeners.forEach { it(get()) }
    }
}