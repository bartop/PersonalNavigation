package pl.polsl.student.personalnavigation

import org.osmdroid.views.overlay.Overlay

/**
 * Created by Bartosz Miera on 2017-12-15.
 */
class MarkerFromBackend(val id: Long, val name: String, val position: Position) : MapMarker{
    override fun toOsmOverlay(): Overlay {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}