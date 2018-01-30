package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java8.util.Optional
import org.osmdroid.util.BoundingBox
import pl.polsl.student.personalnavigation.model.MarkersSource
import pl.polsl.student.personalnavigation.model.IdentifiableMarker
import pl.polsl.student.personalnavigation.model.TrackedMarker
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


class MarkersViewModel(
        private val executor: ScheduledExecutorService,
        private val markersSource: MarkersSource,
        private val boundingBoxTransform: (BoundingBox) -> BoundingBox,
        private val trackedMarkerModel: TrackedMarker
) : ViewModel() {
    private val atomicBoundingBox = AtomicReference<BoundingBox>(
            BoundingBox(0.0, 0.0, 0.0, 0.0)
    )
    private val mutableMarkers: MutableLiveData<Set<IdentifiableMarker>> = MutableLiveData()

    private val mutableTrackedMarker: MutableLiveData<Optional<IdentifiableMarker>> = MutableLiveData()

    private val mutableError: MutableLiveData<Exception> = MutableLiveData()

    init {
        executor.execute { downloadMarkers() }
    }

    fun setBoundingBox(boundingBox: BoundingBox) {
        atomicBoundingBox.set(boundingBoxTransform(boundingBox))
    }

    fun trackMarkerWithId(id: Long) {
        trackedMarkerModel.set(id)
    }

    fun resetTrackedMarker() {
        trackedMarkerModel.reset()
    }

    private fun downloadMarkers() {
        try {
            val downloadedTrackedMarker = trackedMarkerModel.get().map {
                markersSource.getMarker(it)
            }

            val downloadedMarkers = markersSource.getMarkersIn(atomicBoundingBox.get())

            if (!downloadedTrackedMarker.isPresent) {
                if (trackedMarkerModel.get().isPresent) {
                    trackedMarkerModel.reset()
                }
            }

            mutableMarkers.postValue(downloadedMarkers)

            mutableTrackedMarker.postValue(
                    downloadedTrackedMarker
            )
        } catch (e: Exception) {
            mutableError.postValue(e)
        } finally {
            executor.schedule(this::downloadMarkers, 100, TimeUnit.MILLISECONDS)
        }
    }

    val markers: LiveData<Set<IdentifiableMarker>>
        get() = mutableMarkers

    val trackedMarker: LiveData<Optional<IdentifiableMarker>>
        get() = mutableTrackedMarker

    val error: LiveData<Exception>
        get() = mutableError

}