package io.samborskii.nusbus

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.samborskii.nusbus.api.ApiModule
import io.samborskii.nusbus.net.NetModule


class NusBusApplication : Application() {

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .netModule(NetModule())
            .apiModule(ApiModule())
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    companion object {
        fun getComponent(context: Context): AppComponent {
            val application = context.applicationContext as NusBusApplication
            return application.component
        }
    }
}
