package io.samborskii.nusbus

import android.app.Application
import android.content.Context
import com.squareup.leakcanary.LeakCanary
import io.samborskii.nusbus.api.ApiModule
import io.samborskii.nusbus.model.ModelModule
import io.samborskii.nusbus.model.dao.DatabaseModule
import io.samborskii.nusbus.net.NetModule


class NusBusApplication : Application() {

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .netModule(NetModule())
            .apiModule(ApiModule())
            .databaseModule(DatabaseModule(this))
            .modelModule(ModelModule())
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
    }

    companion object {
        fun getComponent(context: Context): AppComponent {
            val application = context.applicationContext as NusBusApplication
            return application.component
        }
    }
}
