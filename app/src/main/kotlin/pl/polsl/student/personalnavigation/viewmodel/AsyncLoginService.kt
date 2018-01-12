package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import pl.polsl.student.personalnavigation.model.AuthenticationData
import pl.polsl.student.personalnavigation.model.LoginService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class AsyncLoginService(
        private val loginService: LoginService,
        private val executor: ScheduledExecutorService
): ViewModel() {
    private val mutableAuthenticationData: MutableLiveData<AuthenticationData> = MutableLiveData()

    private val mutableError: MutableLiveData<Exception> = MutableLiveData()

    fun login() {
        try {
            mutableAuthenticationData.postValue(loginService.login())
        } catch (e: Exception) {
            mutableError.postValue(e)
            executor.schedule(this::login, 500, TimeUnit.MILLISECONDS)
        }
    }

    val authenticationData: LiveData<AuthenticationData>
        get() = mutableAuthenticationData

    val error: LiveData<Exception>
        get() = mutableError
}