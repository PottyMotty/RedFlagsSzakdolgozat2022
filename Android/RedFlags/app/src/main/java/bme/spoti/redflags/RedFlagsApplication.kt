package bme.spoti.redflags

import android.app.Application
import bme.spoti.redflags.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber


class RedFlagsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RedFlagsApplication)
            modules(
                networkModule,
                repositoryModule,
                viewModelModule,
                othersModule
            )
        }
        Timber.plant(Timber.DebugTree())
    }
}