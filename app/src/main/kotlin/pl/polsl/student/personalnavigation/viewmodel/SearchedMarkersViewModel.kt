package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import pl.polsl.student.personalnavigation.model.DistanceMarker
import pl.polsl.student.personalnavigation.model.MarkersSource
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class SearchedMarkersViewModel(
    private val executor: ScheduledExecutorService,
    private val markersSource: MarkersSource
) : ViewModel() {
    private val mutableMarkers: MutableLiveData<List<DistanceMarker>> = MutableLiveData()
    private val mutableError: MutableLiveData<Exception> = MutableLiveData()
    private var keepUpdating = false

    fun startUpdates() {
        if (!keepUpdating) {
            executor.execute { downloadMarkers() }
            keepUpdating = true
        }
    }

    fun stopUpdates() {
        keepUpdating = false
    }

    private fun downloadMarkers() {
        try {
            mutableMarkers.postValue(markersSource.getFilteredMarkers(100))
        } catch (e: Exception) {
            mutableError.postValue(e)
        } finally {
            if (keepUpdating) {
                executor.schedule(this::downloadMarkers, 1000, TimeUnit.MILLISECONDS)
            }
        }
    }

    val markers: LiveData<List<DistanceMarker>> = mutableMarkers

    val error: LiveData<Exception> = mutableError

}