package pl.polsl.student.personalnavigation.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button

import kotlinx.android.synthetic.main.activity_search.*
import kotterknife.bindView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.inject
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.DistanceMarker
import pl.polsl.student.personalnavigation.model.TrackedMarker
import pl.polsl.student.personalnavigation.util.observeNotNull
import pl.polsl.student.personalnavigation.viewmodel.MapViewModel
import pl.polsl.student.personalnavigation.viewmodel.MarkersViewModel
import pl.polsl.student.personalnavigation.viewmodel.SearchedMarkersViewModel

class SearchActivity : AppCompatActivity() {

    private val recyclerView by bindView<RecyclerView>(R.id.markersRecyclerView)
    private val recyclerAdapter by lazy {
        MarkersRecyclerViewAdapter(this::showMarker, this::trackMarker)
    }

    private val searchedMarkersViewModel by lazy {
        getViewModel<SearchedMarkersViewModel>()
    }
    
    private val mapViewModel by lazy {
        getViewModel<MapViewModel>()
    }

    private val markersViewModel by lazy {
        getViewModel<MarkersViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.filtersButton).onClick {
            startActivity<FiltersSettingActivity>()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter

        searchedMarkersViewModel
                .markers
                .observeNotNull(this) {
                    recyclerAdapter.markers = it
                }

        searchedMarkersViewModel.startUpdates()
    }

    override fun onDestroy() {
        searchedMarkersViewModel.stopUpdates()
        super.onDestroy()
    }

    private fun showMarker(marker: DistanceMarker) {
        mapViewModel.setTrackMyself(false)
        mapViewModel.setCenter(marker.position.toGeoPoint())
        this.finish()
    }

    private fun trackMarker(marker: DistanceMarker) {
        mapViewModel.setTrackMyself(false)
        mapViewModel.setCenter(marker.position.toGeoPoint())
        markersViewModel.trackMarkerWithId(marker.id)

        this.finish()
    }
}
