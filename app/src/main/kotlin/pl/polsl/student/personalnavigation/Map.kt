package pl.polsl.student.personalnavigation

import android.util.Log
import com.github.kittinunf.result.Result
import org.osmdroid.views.MapView

/**
 * Created by Bartosz Miera on 2017-12-12.
 */
class Map(
        private val map: MapView,
        private val overlayMarkersFactory: OverlayMarkersFactory
) : MarkersConsumer {

    override fun consume(markers: Iterable<IdentifiableMarker>) {
            map.overlays.clear()
            markers.forEach { marker -> map.overlays.add(overlayMarkersFactory.create(marker)) }
            map.invalidate()
    }
}
