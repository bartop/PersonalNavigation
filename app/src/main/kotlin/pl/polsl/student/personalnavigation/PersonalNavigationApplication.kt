package pl.polsl.student.personalnavigation

import android.app.Application
import org.koin.android.ext.android.startKoin
import pl.polsl.student.personalnavigation.di.koinModule

class PersonalNavigationApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(koinModule))
    }
}