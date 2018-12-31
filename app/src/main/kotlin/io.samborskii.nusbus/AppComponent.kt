package io.samborskii.nusbus

import dagger.Component
import io.samborskii.nusbus.api.ApiModule
import io.samborskii.nusbus.model.ModelModule
import io.samborskii.nusbus.model.dao.DatabaseModule
import io.samborskii.nusbus.net.NetModule
import io.samborskii.nusbus.ui.main.MainActivity
import io.samborskii.nusbus.ui.main.MainActivityPresentationModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetModule::class, ApiModule::class, DatabaseModule::class, ModelModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun newMainPresentationModel(): MainActivityPresentationModel
}
