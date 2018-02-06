package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java8.util.Optional
import org.jetbrains.anko.AnkoLogger
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import pl.polsl.student.personalnavigation.model.TrackedMarker
import pl.polsl.student.personalnavigation.util.calculateDuration
import pl.polsl.student.personalnavigation.util.calculateLength
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledThreadPoolExecutor


class RoadViewModel(
        private val roadManager: RoadManager,
        private val trackedMarker: TrackedMarker
): ViewModel(), AnkoLogger {
    private val distanceLimit = 4
    private val targetReachedLimit = 8
    private val mutableRoad: MutableLiveData<Optional<Road>> = MutableLiveData()

    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private var userPosition: GeoPoint = GeoPoint(0.0, 0.0)
    private var trackedPosition: Optional<GeoPoint> = Optional.empty()

    init {
        scheduledThreadPoolExecutor.maximumPoolSize = 1
        trackedMarker.addListener {
            resetTrackedPosition()
        }
    }

    fun setUserPosition(position: GeoPoint) {
        synchronized(this) {
            if (position.distanceTo(userPosition) > distanceLimit) {
                userPosition = position
                updateRoad()
            }
        }
    }

    fun setTrackedPosition(position: GeoPoint) {
        synchronized(this) {
            val update = trackedPosition
                    .map { position.distanceTo(it) > distanceLimit }
                    .orElse(true)

            if (update) {
                trackedPosition = Optional.of(position)
                updateRoad()
            }
        }
    }

    fun resetTrackedPosition() {
        synchronized(this) {
            if (trackedPosition.isPresent) {
                trackedPosition = Optional.empty()
                mutableRoad.postValue(Optional.empty())
                scheduledThreadPoolExecutor.queue.clear()
            }
        }
    }

    private fun updateRoad() {
        scheduledThreadPoolExecutor.queue.clear()

        trackedPosition
                .ifPresentOrElse(
                        { position ->
                            if (position.distanceTo(userPosition) < targetReachedLimit) {
                                trackedMarker.reset()
                            } else {
                                scheduledThreadPoolExecutor.submit {
                                    trackedMarker.get().ifPresent { id ->
                                        downloadRoad(position, id)
                                    }
                                }
                            }
                        },
                        { mutableRoad.value = Optional.empty() }
                )
    }

    private fun downloadRoad(destination: GeoPoint, prevTrackedId: Long) {
        val userPosition = synchronized(this) {
            this.userPosition
        }

        val road = roadManager.getRoad(ArrayList(listOf(userPosition, destination)))
        road.calculateDuration()
        road.calculateLength()

        synchronized(trackedMarker) {
            trackedMarker.get().ifPresentOrElse(
                    {
                        if (it == prevTrackedId) {
                            mutableRoad.postValue(Optional.of(road))
                        }
                    },
                    {
                        mutableRoad.postValue(Optional.empty())
                    }
            )
        }

    }

    val road: LiveData<Optional<Road>> = mutableRoad
}