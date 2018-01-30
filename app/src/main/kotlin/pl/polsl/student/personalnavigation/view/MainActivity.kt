package pl.polsl.student.personalnavigation.view

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import com.github.kittinunf.result.failure
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import java8.util.Optional
import kotterknife.bindView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onTouch
import org.jetbrains.anko.toast
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.LocationSender
import pl.polsl.student.personalnavigation.util.SimpleMapListener
import pl.polsl.student.personalnavigation.util.observeNotNull
import pl.polsl.student.personalnavigation.util.thenAcceptOnUiThread
import pl.polsl.student.personalnavigation.viewmodel.MarkersViewModel
import pl.polsl.student.personalnavigation.viewmodel.NameViewModel
import pl.polsl.student.personalnavigation.viewmodel.RoadViewModel
import pl.polsl.student.personalnavigation.viewmodel.UserIdViewModel


class MainActivity : AppCompatActivity(), AnkoLogger {
    private val handler = Handler()

    private val markersViewModel by lazy { getViewModel<MarkersViewModel>() }
    private val nameViewModel by lazy { getViewModel<NameViewModel>() }
    private val userIdViewModel by lazy { getViewModel<UserIdViewModel>() }
    private val roadViewModel by lazy { getViewModel<RoadViewModel>() }

    private val locationSender: LocationSender by inject()

    private val mapView: MapView by bindView(R.id.map)
    private val nameView: TextView by bindView(R.id.nameTextView)
    private val trackButton: ToggleButton by bindView(R.id.trackButton)
    private val cancelTrackButton: ImageButton by bindView(R.id.cancelTrackButton)
    private val locationControl by lazy {
        SmartLocation
                .with(this@MainActivity)
                .location(LocationManagerProvider())
                .continuous()
                .config(
                        LocationParams.NAVIGATION
                )
    }
    private var currentLocation = Optional.empty<Location>()

    //Depends on `mapView` - use only after layout inflation
    private val markersFactory: OverlayMarkersFactory by lazy {
        DefaultOverlayMarkersFactory(this, mapView, this::onMarkerLongPressed)
    }

    private val map by lazy {
        Map(
                mapView,
                markersFactory
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

        with(markersViewModel) {
            markers.observeNotNull(this@MainActivity) {
                map.consume(it)
                roadViewModel.consume(it)
            }

            error.observeNotNull(this@MainActivity) {
                error("Cannot download markers!", it)
            }
        }

        userIdViewModel.userId.observeNotNull(this) {
            markersFactory.setUserId(it)
        }

        mapView.setMapListener(
                SimpleMapListener {
                    markersViewModel.setBoundingBox(mapView.boundingBox)
                }
        )

        trackButton.setOnClickListener {
            if (trackButton.isChecked) {
                mapView.isFlingEnabled
                currentLocation.ifPresent {
                    mapView.controller.setCenter(GeoPoint(it))
                }
            }
        }

        currentLocation = Optional.ofNullable(locationControl.lastLocation)

        locationControl
                .start {
                    runOnUiThread {
                        currentLocation = Optional.of(it)
                        if (trackButton.isChecked) {
                            mapView.controller.setCenter(GeoPoint(it))
                        }
                    }
                }

        nameViewModel.name.observeNotNull(this) {
            nameInputDialog.setName(it)
            nameView.text = it
        }

        if (nameViewModel.name.value == null) {
            nameInputDialog.show()
        }

        mapView.onTouch {
            v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                trackButton.isChecked = false
            }
        }

        roadViewModel
                .road
                .observeNotNull(this) {
                    it.ifPresentOrElse(
                            map::showRoad,
                            map::clearRoad
                    )
                }

        postLocation()

        cancelTrackButton.onClick { cancelTracking() }
    }

    private fun postLocation() {
        currentLocation.ifPresent {
                    locationSender
                            .postLocation(it, nameViewModel.name.value ?: "Noname")
                            .thenAcceptOnUiThread(this) {
                                    it.failure {
                                        toast(it.message.toString())
                                    }
                                    handler.postDelayed(this::postLocation, 500)
                            }
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

    fun showNameDialog(v: View?) {
        nameInputDialog.show()
    }

    private fun cancelTracking() {
        roadViewModel.stopTracking()
        markersFactory.resetTrackedId()
        cancelTrackButton.visibility = View.INVISIBLE
    }

    private fun onNameEntered(name: String) {
        try {
            nameViewModel.setName(name)
        } catch (e: Exception) {
            toast(e.message.toString())
        }
    }

    private fun onMarkerLongPressed(marker: CustomMarker) {
        markersFactory.setTrackedId(marker.model.id)
        roadViewModel.setTrackedId(marker.model.id)
        cancelTrackButton.visibility = View.VISIBLE
    }
}