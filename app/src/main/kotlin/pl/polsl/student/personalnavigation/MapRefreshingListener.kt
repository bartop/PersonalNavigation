package pl.polsl.student.personalnavigation

import android.os.Parcelable
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.views.MapView

class MapRefreshingListener(
        private val markersSource: MarkersSource,
        private val asynchronousMarkersConsumer: AsynchronousMarkersConsumer) : MapListener {

    override fun onZoom(event: ZoomEvent): Boolean = setVisibleOverlays(event.source)

    override fun onScroll(event: ScrollEvent): Boolean = setVisibleOverlays(event.source)

    private fun setVisibleOverlays(map: MapView) : Boolean {
        asynchronousMarkersConsumer.consume(markersSource.toMarkersFuture(map.boundingBox))
        return true
    }

}