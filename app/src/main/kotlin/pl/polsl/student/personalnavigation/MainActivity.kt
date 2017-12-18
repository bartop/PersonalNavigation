package pl.polsl.student.personalnavigation

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import com.github.kittinunf.result.Result
import com.yayandroid.locationmanager.base.LocationBaseActivity
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import kotterknife.bindView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.concurrent.ScheduledThreadPoolExecutor


class MainActivity : LocationBaseActivity() {
    private val TAG = "MainActivity"
    private val NAME_KEY = "name"
    private val serverUrl = "http://10.0.2.2:8080"
    private val boundingBoxTransform = ScalingBoundingBoxTransform(2.0f)
    private val threadPoolExecutor = ScheduledThreadPoolExecutor(8)
    private val asyncMarkersSource = DefaultAsyncMarkersSource(
            BackendMarkersSource(serverUrl),
            threadPoolExecutor
    )
    private val handler = Handler()
    private val loginService by lazy {
        AsyncLoginService(
            BackendLoginService(serverUrl, sharedPreferences()),
            threadPoolExecutor
        )
    }
    private val locationSender: LocationSender by lazy {
        LocationSender(
                serverUrl,
                {
                    nameView.text.toString()
                }
        )
    }

    private val mapView: MapView by bindView(R.id.map)
    private val nameView: TextView by bindView(R.id.nameTextView)
    private val trackButton: ToggleButton by bindView(R.id.trackButton)
    private var currentLocation: Location? = null

    //Depends on `mapView` - use only after layout inflation
    private val markersConsumer: MarkersConsumer by lazy {
        Map(
                mapView,
                DefaultOverlayMarkersFactory(mapView)
        )
    }

    private val nameInputDialog by lazy {
        NameInputDialog(this, layoutInflater, this::onNameEntered)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main)

        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(9)

        if (!sharedPreferences().contains(NAME_KEY)) {
            showNameDialog(null)
        } else {
            nameView.text = sharedPreferences().getString(NAME_KEY, getString(R.string.your_name))
        }

        getLocation()
        refreshLocation()
        login()
    }


    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }

    private fun login() {
        loginService
                .login()
                .exceptionally {
                    Result.of { throw it }
                }
                .thenAccept {
                    runOnUiThread {
                        it.fold(
                                {
                                    updateMap()
                                },
                                {
                                    //try to relogin
                                    handler.postDelayed(this::login, 1000L)
                                    Log.e(TAG, "Cannot login!", it)
                                }
                        )
                    }
                }
    }

    private fun updateMap() {
        val delay = 250L
        asyncMarkersSource
                .getMarkersIn(
                        boundingBoxTransform(mapView.boundingBox)
                )
                .exceptionally {
                    Result.of { throw it }
                }
                .thenAccept {
                    markers -> runOnUiThread {
                        markersConsumer.consume(markers)
                        handler.postDelayed(this::updateMap, delay)
                    }
                }
    }

    private fun refreshLocation() {
        val location = currentLocation
        handler.postDelayed(this::refreshLocation, 1000L)
        if (location != null) {
            if (trackButton.isChecked) {
                mapView.controller.setCenter(GeoPoint(currentLocation))
            }

            locationSender.postLocation(location)
        }
    }

    fun showNameDialog(view: View?) {
        nameInputDialog.show()
    }

    private fun onNameEntered(name: String) {
        nameView.text = name
        sharedPreferences().edit().putString(NAME_KEY, name).apply()
    }

    private fun sharedPreferences(): SharedPreferences {
        return getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        )
    }

    override fun onLocationFailed(type: Int) {
        Log.e(TAG, "On location failed: $type")
        handler.postDelayed(this::getLocation, 500)
    }

    override fun getLocationConfiguration(): LocationConfiguration {
        return LocationConfiguration.Builder()
                //.keepTracking(true)
                .askForPermission(
                        PermissionConfiguration.Builder().rationaleMessage(
                        "Need location permission"
                        ).build()
                )
                .useDefaultProviders(
                        DefaultProviderConfiguration
                                .Builder()
                                .build()
                )
                .build()
    }

    override fun onLocationChanged(location: Location) {
        Log.i(TAG, location.toString())
        currentLocation = location
        if (trackButton.isChecked) {
            mapView.controller.setCenter(GeoPoint(location))
        }
        handler.postDelayed(this::getLocation, 100)
    }


}