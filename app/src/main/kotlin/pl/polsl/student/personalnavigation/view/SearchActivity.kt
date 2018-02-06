package pl.polsl.student.personalnavigation.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_search.*
import kotterknife.bindView
import org.koin.android.architecture.ext.getViewModel
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.util.observeNotNull
import pl.polsl.student.personalnavigation.viewmodel.SearchedMarkersViewModel

class SearchActivity : AppCompatActivity() {

    private val recyclerView by bindView<RecyclerView>(R.id.markersRecyclerView)
    private val recyclerAdapter by lazy {
        MarkersRecyclerViewAdapter()
    }

    private val markersViewModel by lazy {
        getViewModel<SearchedMarkersViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter

        markersViewModel
                .markers
                .observeNotNull(this) {
                    recyclerAdapter.markers = it
                }

        markersViewModel.startUpdates()
    }

    override fun onDestroy() {
        markersViewModel.stopUpdates()
        super.onDestroy()
    }
}
