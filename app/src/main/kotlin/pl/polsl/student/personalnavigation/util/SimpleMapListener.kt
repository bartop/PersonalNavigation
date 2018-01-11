package pl.polsl.student.personalnavigation.util

import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent


class SimpleMapListener(
        private val callback: () -> Unit
): MapListener {
    override fun onScroll(event: ScrollEvent?): Boolean {
        callback()
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        callback()
        return true
    }

}