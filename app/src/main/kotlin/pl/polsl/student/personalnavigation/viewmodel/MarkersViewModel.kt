package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java8.util.Optional
import org.osmdroid.util.BoundingBox
import pl.polsl.student.personalnavigation.model.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


class MarkersViewModel(
        private val executor: ScheduledExecutorService,
        private val markersSource: MarkersSource,
        private val boundingBoxTransform: (BoundingBox) -> BoundingBox,
        private val trackedMarkerModel: TrackedMarker,
        private val authenticationService: AuthenticationService
) : ViewModel() {
    private val atomicBoundingBox = AtomicReference<BoundingBox>(
            BoundingBox(0.0, 0.0, 0.0, 0.0)
    )

    private val mutableUserMarker: MutableLiveData<IdentifiableMarker> = MutableLiveData()

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
            val trackedMarker = trackedMarkerModel.get()
            val downloadedTrackedMarker = trackedMarker.flatMap {
                try {
                    Optional.of(markersSource.getMarker(it))
                } catch (e: Exception) {
                    Optional.empty<IdentifiableMarker>()
                }
            }

            val downloadedMarkers = markersSource.getMarkersIn(atomicBoundingBox.get())

            val userId = authenticationService.authentication().id

            val userMarker = downloadedMarkers
                    .firstOrNull { it.id == userId }
                    ?: markersSource.getMarker(userId)

            if (!downloadedTrackedMarker.isPresent && trackedMarker.isPresent) {
                trackedMarkerModel.reset()
            }

            mutableUserMarker.postValue(userMarker)

            mutableMarkers.postValue(downloadedMarkers)

            synchronized(trackedMarkerModel) {
                trackedMarkerModel.get().ifPresentOrElse(
                        { trackedId ->
                            if (downloadedTrackedMarker.map { it.id == trackedId}.orElse(false)) {
                                mutableTrackedMarker.postValue(
                                        downloadedTrackedMarker
                                )
                            } else {
                                mutableTrackedMarker.postValue(Optional.empty())
                            }
                        },
                        {
                            mutableTrackedMarker.postValue(Optional.empty())
                        }
                )
            }
        } catch (e: Exception) {
            mutableError.postValue(e)
        } finally {
            executor.schedule(this::downloadMarkers, 100, TimeUnit.MILLISECONDS)
        }
    }

    val markers: LiveData<Set<IdentifiableMarker>> = mutableMarkers

    val trackedMarker: LiveData<Optional<IdentifiableMarker>> = mutableTrackedMarker

    val userMarker: LiveData<IdentifiableMarker> = mutableUserMarker

    val error: LiveData<Exception> = mutableError

}