package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java8.util.Optional
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.osmdroid.bonuspack.routing.Road
import pl.polsl.student.personalnavigation.model.AuthenticationService
import pl.polsl.student.personalnavigation.model.IdentifiableMarker
import pl.polsl.student.personalnavigation.model.RoadProducer
import pl.polsl.student.personalnavigation.view.MarkersConsumer


class RoadViewModel(
        private val authenticationService: AuthenticationService,
        private val roadProducer: RoadProducer
): ViewModel(), AnkoLogger, MarkersConsumer {
    private var trackedId: Optional<Long> = Optional.empty()
    private val mutableRoad: MutableLiveData<Optional<Road>> = MutableLiveData()

    override fun consume(markers: Iterable<IdentifiableMarker>) {
        try {
            val userMarker = markers.first { it.id == authenticationService.authentication().id }
            val trackedMarker = trackedId.map { id ->
                markers.first { it.id == id }
            }

            trackedMarker.ifPresentOrElse(
                    {
                        roadProducer
                                .roadBetween(
                                        userMarker.position.toGeoPoint(),
                                        it.position.toGeoPoint()
                                )
                                .thenAccept {
                                    mutableRoad.postValue(Optional.of(it))
                                }
                    },
                    {
                        mutableRoad.postValue(Optional.empty())
                    }
            )

        } catch (e: Exception) {
            error("Cannot find the road!", e)
        }
    }

    fun setTrackedId(id: Long) {
        trackedId = Optional.of(id)
    }

    fun stopTracking() {
        trackedId = Optional.empty()
    }

    val road: LiveData<Optional<Road>> = mutableRoad
}