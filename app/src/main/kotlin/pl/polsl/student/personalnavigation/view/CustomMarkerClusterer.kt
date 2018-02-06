package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.graphics.Canvas
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.clustering.StaticCluster
import org.osmdroid.views.MapView
import java.util.ArrayList


class CustomMarkerClusterer(ctx: Context?) : RadiusMarkerClusterer(ctx) {

    override fun renderer(clusters: ArrayList<StaticCluster>, canvas: Canvas, mapView: MapView) {
        for (cluster in clusters) {
            if (cluster.getSize() == 1) {
                //cluster has only 1 marker => use it as it is:
                cluster.getItem(0).showInfoWindow()
                cluster.setMarker(cluster.getItem(0))
            } else {
                //only draw 1 Marker at Cluster center, displaying number of Markers contained
                val m = buildClusterMarker(cluster, mapView)

                for (i in 0 until cluster.size) {
                    cluster.getItem(i).closeInfoWindow()
                }

                cluster.setMarker(m)
            }
        }
    }
}