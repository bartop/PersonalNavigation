package pl.polsl.student.personalnavigation.di

import android.content.Context
import android.content.ContextWrapper
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.*
import pl.polsl.student.personalnavigation.util.ScalingBoundingBoxTransform
import pl.polsl.student.personalnavigation.viewmodel.AsyncLoginService
import pl.polsl.student.personalnavigation.viewmodel.MarkersViewModel
import pl.polsl.student.personalnavigation.viewmodel.NameViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor


val koinModule = applicationContext {
    provide("serverUrl") { "http://10.0.2.2:8080" }
    provide { Executors.newScheduledThreadPool(8) as ScheduledExecutorService }
    provide { get<ScheduledExecutorService>() as Executor }

    provide { BackendMarkersSource(get("serverUrl")) as MarkersSource }
    provide { BackendAuthenticationService(get(), get()) as AuthenticationService }
    provide {
        val context = get<Context>()

        ContextWrapper(context)
                .getSharedPreferences(
                        context.getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                )
    }
    provide { LocationSender(get("serverUrl"), get(), get()) }
    viewModel { MarkersViewModel(get(), get(), ScalingBoundingBoxTransform(2.0f)) }
    viewModel { AsyncLoginService(get(), get()) }
    viewModel { NameViewModel(get()) }
}