package pl.polsl.student.personalnavigation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java8.util.Optional
import org.osmdroid.util.BoundingBox
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


class MarkersViewModel(
        private val executor: ScheduledExecutorService,
        private val markersSource: MarkersSource,
        private val boundingBoxTransform: (BoundingBox) -> BoundingBox
) : ViewModel() {
    private val atomicBoundingBox = AtomicReference<BoundingBox>(
            BoundingBox(0.0, 0.0, 0.0, 0.0)
    )

    private val trackedMarkerId = AtomicReference<Optional<Long>>(Optional.empty())

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
        trackedMarkerId.set(Optional.of(id))
    }

    fun resetTrackedMarker() {
        trackedMarkerId.set(Optional.empty())
    }

    private fun downloadMarkers() {
        try {
            val downloadedTrackedMarker = trackedMarkerId.get().map {
                markersSource.getMarker(it)
            }

            val downloadedMarkers = markersSource.getMarkersIn(atomicBoundingBox.get())

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