package io.samborskii.nusbus

import dagger.Component
import io.samborskii.nusbus.api.ApiModule
import io.samborskii.nusbus.net.NetModule
import io.samborskii.nusbus.ui.main.MainActivity
import io.samborskii.nusbus.ui.main.MainPresentationModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetModule::class, ApiModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)

    fun newMainPresentationModel(): MainPresentationModel
}
