package pl.polsl.student.personalnavigation.di

import android.content.Context
import android.content.ContextWrapper
import org.koin.dsl.module.applicationContext
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.model.*
import pl.polsl.student.personalnavigation.util.ScalingBoundingBoxTransform
import pl.polsl.student.personalnavigation.view.DefaultOverlayMarkersFactory
import pl.polsl.student.personalnavigation.view.OverlayMarkersFactory
import pl.polsl.student.personalnavigation.viewmodel.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

val koinModule = applicationContext {
    provide("serverUrl") { get<Context>().resources.getString(R.string.server_url) }
    provide {
        {
            val roadManager = MapQuestRoadManager(MapQuestApiKey)
            roadManager.addRequestOption("routeType=pedestrian")
            roadManager
        }() as RoadManager
    }
    provide { TrackedMarker() }
    provide { Executors.newScheduledThreadPool(8) as ScheduledExecutorService }
    provide { get<ScheduledExecutorService>() as Executor }
    provide { RoadProducer(get(), get()) }
    provide { FilterDataRepository(get()) }

    provide { BackendMarkersSource(get("serverUrl"), get(), get()) as MarkersSource }
    provide { BackendAuthenticationService(get(), get()) as AuthenticationService }
    provide {
        val context = get<Context>()

        ContextWrapper(context)
                .getSharedPreferences(
                        context.getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                )
    }
    provide { DefaultOverlayMarkersFactory(get(), get()) as OverlayMarkersFactory }
    provide { LocationSender(get("serverUrl"), get(), get()) }
    provide { MarkersViewModel(get(), get(), ScalingBoundingBoxTransform(4.0f), get(), get()) }
    provide { UserIdViewModel(get(), get()) }
    provide { NameViewModel(get()) }
    provide { RoadViewModel(get(), get()) }
    provide { MapViewModel(get()) }
    provide { FilterDataViewModel(get()) }
    provide { SearchedMarkersViewModel(get(), get()) }
}