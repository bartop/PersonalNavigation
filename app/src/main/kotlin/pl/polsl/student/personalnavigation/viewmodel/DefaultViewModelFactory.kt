package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pl.polsl.student.personalnavigation.util.ScalingBoundingBoxTransform
import pl.polsl.student.personalnavigation.model.BackendMarkersSource
import java.util.concurrent.ScheduledExecutorService


class DefaultViewModelFactory(
        private val executor: ScheduledExecutorService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MarkersViewModel::class.java) ->
                MarkersViewModel(
                        executor = executor,
                        markersSource = BackendMarkersSource("http://10.0.2.2:8080"),
                        boundingBoxTransform = ScalingBoundingBoxTransform(2.0f)
                )
            else -> throw Exception("Unknown ViewModel: $modelClass")
        } as T
    }
}