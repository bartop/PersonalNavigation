package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java8.util.Optional
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.util.GeoPoint
import pl.polsl.student.personalnavigation.model.AuthenticationService
import pl.polsl.student.personalnavigation.model.IdentifiableMarker
import pl.polsl.student.personalnavigation.model.RoadProducer
import pl.polsl.student.personalnavigation.model.TrackedMarker
import pl.polsl.student.personalnavigation.view.MarkersConsumer


class RoadViewModel(
        private val roadProducer: RoadProducer,
        private val trackedMarker: TrackedMarker
): ViewModel(), AnkoLogger {
    private val distanceLimit = 4
    private val mutableRoad: MutableLiveData<Optional<Road>> = MutableLiveData()

    private var userPosition: GeoPoint = GeoPoint(0.0, 0.0)
    private var trackedPosition: Optional<GeoPoint> = Optional.empty()

    fun setUserPosition(position: GeoPoint) {
        if (position.distanceTo(userPosition) > distanceLimit) {
            userPosition = position
            updateRoad()
        }
    }

    fun setTrackedPosition(position: GeoPoint) {
        val update = trackedPosition
                .map { position.distanceTo(it) > distanceLimit }
                .orElse(true)

        if (update) {
            trackedPosition = Optional.of(position)
            updateRoad()
        }

    }

    fun resetTrackedPosition() {
        if (trackedPosition.isPresent) {
            trackedPosition = Optional.empty()
            mutableRoad.postValue(Optional.empty())
        }
    }

    fun updateRoad() {
        trackedPosition
                .ifPresent {
                    if (it.distanceTo(userPosition) < distanceLimit) {
                        trackedMarker.reset()
                        mutableRoad.postValue(Optional.empty())
                    } else {
                        roadProducer
                                .roadBetween(
                                        userPosition,
                                        it
                                )
                                .thenAccept {
                                    mutableRoad.postValue(Optional.of(it))
                                }
                    }
                }
    }

    val road: LiveData<Optional<Road>> = mutableRoad
}