package pl.polsl.student.personalnavigation.di

import android.content.Context
import android.content.ContextWrapper
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.BackendLoginService
import pl.polsl.student.personalnavigation.model.BackendMarkersSource
import pl.polsl.student.personalnavigation.model.LoginService
import pl.polsl.student.personalnavigation.model.MarkersSource
import pl.polsl.student.personalnavigation.util.ScalingBoundingBoxTransform
import pl.polsl.student.personalnavigation.viewmodel.AsyncLoginService
import pl.polsl.student.personalnavigation.viewmodel.MarkersViewModel
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor


val koinModule = applicationContext {
    provide("serverUrl") { "http://10.0.2.2:8080" }
    provide { ScheduledThreadPoolExecutor(8) as ScheduledExecutorService }
    provide { BackendMarkersSource(get("serverUrl")) as MarkersSource }
    provide { BackendLoginService(get(), get()) as LoginService }
    provide {
        val context = get<Context>()

        ContextWrapper(context)
                .getSharedPreferences(
                        context.getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                )
    }
    viewModel { MarkersViewModel(get(), get(), ScalingBoundingBoxTransform(2.0f)) }
    viewModel { AsyncLoginService(get(), get()) }
}