package pl.polsl.student.personalnavigation.view

import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayWithIW
import org.osmdroid.views.overlay.infowindow.InfoWindow
import pl.polsl.student.personalnavigation.R

class CustomInfoWindow(mapView: MapView) : InfoWindow(R.layout.custom_info_window, mapView) {
    override fun onOpen(item: Any?) {
        val overlay = item as? OverlayWithIW
        val title = overlay?.title ?: ""

        mView.findViewById<TextView>(R.id.bubbleNameText).text = title
    }

    override fun onClose() {

    }

}