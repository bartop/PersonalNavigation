package pl.polsl.student.personalnavigation.view

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
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
import org.jetbrains.anko.info
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
import pl.polsl.student.personalnavigation.viewmodel.*
import pl.polsl.student.personalnavigation.util.*

class MainActivity : AppCompatActivity(), AnkoLogger {
    private val handler = Handler()

    private val markersViewModel by lazy { getViewModel<MarkersViewModel>() }
    private val nameViewModel by lazy { getViewModel<NameViewModel>() }
    private val userIdViewModel by lazy { getViewModel<UserIdViewModel>() }
    private val roadViewModel by lazy { getViewModel<RoadViewModel>() }
    private val mapViewModel by lazy { getViewModel<MapViewModel>() }

    private val locationSender: LocationSender by inject()

    private val nameLayout: View by bindView(R.id.nameLayout)
    private val directionsLayout: View by bindView(R.id.directionsLayout)
    private val directionTextView: TextView by bindView(R.id.directionTextView)
    private val durationTextView: TextView by bindView(R.id.durationTextView)

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

        with (supportActionBar!!) {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.custom_action_bar_layout)
        }

        mapView.controller.setZoom(mapViewModel.zoom.value!!)
        mapView.controller.setCenter(mapViewModel.center.value)

        mapViewModel.center.observeNotNull(this) {
            if (!it.hasSameLatitudeLongitude(mapView.mapCenter)) {
                mapView.controller.setCenter(it)
            }
        }

        mapView.setMultiTouchControls(true)

        with(markersViewModel) {
            markers.observeNotNull(this@MainActivity) {
                map.consume(it)
            }

            userMarker.observeNotNull(this@MainActivity) {
                roadViewModel.setUserPosition(it.position.toGeoPoint())
            }

            trackedMarker.observeNotNull(this@MainActivity) {
                it.ifPresentOrElse(
                        { roadViewModel.setTrackedPosition(it.position.toGeoPoint()) },
                        { roadViewModel.resetTrackedPosition() }
                )
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

                    mapViewModel.setZoom(mapView.getZoomLevel(false))
                    mapViewModel.setCenter(
                            mapView.mapCenter.toGeoPoint()
                    )
                }
        )

        trackButton.setOnClickListener {
            if (trackButton.isChecked) {
                currentLocation.ifPresent {
                    mapViewModel.setCenter(GeoPoint(it))
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
            _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                trackButton.isChecked = false
            }
        }

        nameView.onClick {
            showNameDialog()
        }

        roadViewModel
                .road
                .observeNotNull(this) {
                    it.ifPresentOrElse(
                            map::showRoad,
                            map::clearRoad
                    )
                }

        roadViewModel
                .road
                .observeNotNull(this) {
                    it.ifPresentOrElse(
                            {
                                nameLayout.visibility = View.INVISIBLE
                                directionsLayout.visibility = View.VISIBLE
                                with (it.mNodes) {
                                    directionTextView.text = getOrElse(1) { first() }.mInstructions
                                }
                                durationTextView.text = it.getLengthDurationText(this, 0)


                            },
                            {
                                nameLayout.visibility = View.VISIBLE
                                directionsLayout.visibility = View.INVISIBLE
                            }
                    )
                }

        markersViewModel
                .trackedMarker
                .observeNotNull(this) {
                    it.ifPresentOrElse(
                            { markersFactory.setTrackedId(it.id) },
                            markersFactory::resetTrackedId
                    )
                }

        markersViewModel
                .trackedMarker
                .observeNotNull(this) {
                    it.ifPresentOrElse(
                            { cancelTrackButton.visibility = View.VISIBLE },
                            { cancelTrackButton.visibility = View.INVISIBLE }
                    )
                }

        cancelTrackButton.onClick { cancelTracking() }
    }

    private fun postLocation() {
        currentLocation
                .ifPresentOrElse(
                        {
                            locationSender
                                    .postLocation(it, nameViewModel.name.value ?: "Noname")
                                    .thenAcceptOnUiThread(this) {
                                            it.failure {
                                                toast(it.message.toString())
                                            }
                                            handler.postDelayed(this::postLocation, 500)
                                    }
                        },
                        { handler.postDelayed(this::postLocation, 500) }
                )
    }


    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val lastLocation = locationControl.lastLocation

        if (lastLocation != null) {
            updateLocation(lastLocation)
        }

        locationControl
                .start {
                    runOnUiThread {
                        updateLocation(it)
                    }
                }

        postLocation()
    }

    private fun showNameDialog() {
        nameInputDialog.show()
    }

    private fun updateLocation(location: Location) {
            currentLocation = Optional.of(location)
            if (location.hasBearing()) {
                info("Setting bearing ${location.bearing}")
                markersFactory.setUserBearing(location.bearing)
            } else {
                markersFactory.resetUserBearing()
            }
            if (trackButton.isChecked) {
                mapViewModel.setCenter(GeoPoint(location))
            }
    }

    private fun cancelTracking() {
        markersViewModel.resetTrackedMarker()
    }

    private fun onNameEntered(name: String) {
        try {
            nameViewModel.setName(name)
        } catch (e: Exception) {
            toast(e.message.toString())
        }
    }

    private fun onMarkerLongPressed(marker: CustomMarker) {
        markersViewModel.trackMarkerWithId(marker.model.id)
    }
}