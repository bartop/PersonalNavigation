package pl.polsl.student.personalnavigation

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import kotterknife.bindView
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import java.util.concurrent.ScheduledThreadPoolExecutor

class MainActivity : Activity() {
    private val boundingBoxTransform = ScalingBoundingBoxTransform(2.0f)
    private val threadPoolExecutor = ScheduledThreadPoolExecutor(8)
    private val markersSource: MarkersSource = BackendMarkersSource("http://10.0.2.2:8080/")
    private val asyncMarkersSource = DefaultAsyncMarkersSource(
            markersSource,
            threadPoolExecutor
    )


    private val mapView: MapView by bindView(R.id.map)

    //Depends on `mapView` - use only after layout inflation
    private val markersConsumer: AsynchronousMarkersConsumer by lazy{
        Map(mapView, threadPoolExecutor, this::runOnUiThread)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main)

        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        //TODO: remove magic number
        FixedRateScheduledTask(1000) {
            markersConsumer.consume(
                asyncMarkersSource.getMarkersIn(
                        boundingBoxTransform(mapView.boundingBox)
                )
            )
        }
    }


    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }
}