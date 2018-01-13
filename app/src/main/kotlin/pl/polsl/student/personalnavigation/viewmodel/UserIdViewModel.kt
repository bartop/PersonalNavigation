package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import pl.polsl.student.personalnavigation.model.AuthenticationData
import pl.polsl.student.personalnavigation.model.AuthenticationService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class UserIdViewModel(
        private val authenticationService: AuthenticationService,
        private val executor: ScheduledExecutorService
): ViewModel() {
    private val mutableUserId: MutableLiveData<Long> = MutableLiveData()
    private val mutableError: MutableLiveData<Exception> = MutableLiveData()

    init {
        executor.execute {
            loadId()
        }
    }

    private fun loadId() {
        try {
            mutableUserId.postValue(authenticationService.authentication().id)
        } catch (e: Exception) {
            mutableError.postValue(e)
            executor.schedule(this::loadId, 500, TimeUnit.MILLISECONDS)
        }
    }

    val userId: LiveData<Long> = mutableUserId

    val error: LiveData<Exception> = mutableError
}