package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pl.polsl.student.personalnavigation.model.IdentifiableMarker


class CustomMarker(
        mapView: MapView,
        context: Context,
        icon: Drawable,
        val model: IdentifiableMarker,
        val onPressListener: (CustomMarker) -> Unit
) : Marker(mapView, context) {

    init {
        setIcon(icon)
        position  = model.position.toGeoPoint()
        title = model.name
        infoWindow = CustomInfoWindow(mapView)
        showInfoWindow()
        setOnMarkerClickListener { _, _ -> onPressListener(this) == Unit }
    }

    override fun onLongPress(event: MotionEvent?, mapView: MapView?): Boolean {
        onPressListener(this)
        return super.onLongPress(event, mapView)
    }

    override fun onDoubleTap(e: MotionEvent?, mapView: MapView?): Boolean {
        onPressListener(this)
        return super.onDoubleTap(e, mapView)
    }
}