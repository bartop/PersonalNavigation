package pl.polsl.student.personalnavigation.view

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MotionEvent
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
import org.jetbrains.anko.sdk25.coroutines.onTouch
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.*
import pl.polsl.student.personalnavigation.viewmodel.*
import pl.polsl.student.personalnavigation.util.*
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import org.jetbrains.anko.sdk25.coroutines.onClick


class MainActivity : AppCompatActivity(), AnkoLogger {
    private val handler = Handler()

    private val markersViewModel by lazy { getViewModel<MarkersViewModel>() }
    private val nameViewModel by lazy { getViewModel<NameViewModel>() }
    private val userIdViewModel by lazy { getViewModel<UserIdViewModel>() }
    private val roadViewModel by lazy { getViewModel<RoadViewModel>() }
    private val mapViewModel by lazy { getViewModel<MapViewModel>() }

    private lateinit var actionBar: CustomActionBar

    private val locationSender: LocationSender by inject()

    private val mapView: MapView by bindView(R.id.map)
    private val trackButton: ToggleButton by bindView(R.id.trackButton)
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
        val trackedMarker: TrackedMarker by inject()
        DefaultOverlayMarkersFactory(this, mapView, trackedMarker, this::onMarkerLongPressed)
    }

    private val map by lazy {
        MapManager(
                mapView,
                markersFactory
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main)

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
            mapViewModel.setTrackMyself(trackButton.isChecked)
        }

        mapViewModel.trackMyself.observeNotNull(this) {
            trackButton.isChecked = it
            if (it) {
                currentLocation.ifPresent {
                    mapViewModel.setCenter(GeoPoint(it))
                }
            }
        }

        mapView.onTouch {
            _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                mapViewModel.setTrackMyself(false)
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

        actionBar = CustomActionBar(this)

        findViewById<Button>(R.id.searchButton).onClick { startSearchActivity() }
    }

    private fun postLocation() {
        currentLocation
                .ifPresentOrElse(
                        {
                            locationSender
                                    .postLocation(
                                            DefaultMarker(
                                                    name = nameViewModel.name.value ?: "Noname",
                                                    position = Position.fromLocation(it),
                                                    gender = nameViewModel.gender.value ?: Gender.Female,
                                                    skill = nameViewModel.skill.value ?: Skill.Low
                                            )
                                    )
                                    .thenAcceptOnUiThread(this) {
                                            it.failure {
                                                error("Cannot post location", it)
                                            }
                                            handler.postDelayed(this::postLocation, 2000)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_menu_item ->
                startSearchActivity()
            R.id.my_profile_menu_item ->
                actionBar.showProfileDialog()
            R.id.filters_menu_item ->
                startActivity(Intent(this, FiltersSettingActivity::class.java))
            else ->
                    return false
        }
        return true
    }

    private fun startSearchActivity() {
        startActivity(Intent(this, SearchActivity::class.java))
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

    private fun updateLocation(location: Location) {
            currentLocation = Optional.of(location)
            if (location.hasBearing()) {
                info("Setting bearing ${location.bearing}")
                markersFactory.setUserBearing(location.bearing)
            } else {
                markersFactory.resetUserBearing()
            }
            if (mapViewModel.trackMyself.value == true) {
                mapViewModel.setCenter(GeoPoint(location))
            }
    }

    private fun onMarkerLongPressed(marker: CustomMarker) {
        val userId = userIdViewModel.userId.value

        if (userId != null && marker.model.id != userId) {
            marker.setIcon(
                    ResourcesCompat.getDrawable(this.resources, R.drawable.marker_cyan, null)!!
            )
            mapView.invalidate()
            markersViewModel.trackMarkerWithId(marker.model.id)
        }
    }
}