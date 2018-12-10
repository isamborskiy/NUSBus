package io.samborskii.nusbus.ui.main

import android.os.Bundle
import android.util.Log
import io.samborskii.nusbus.NusBusApplication
import io.samborskii.nusbus.R
import io.samborskii.nusbus.api.NusBusClient
import me.dmdev.rxpm.base.PmSupportActivity
import javax.inject.Inject

class MainActivity : PmSupportActivity<MainPresentationModel>() {

    @Inject
    lateinit var client: NusBusClient

    override fun onCreate(savedInstanceState: Bundle?) {
        NusBusApplication.getComponent(this).inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBindPresentationModel(pm: MainPresentationModel) {
        pm.data.observable bindTo { Log.i("TEST", "Size: ${it.size}") }

        pm.errorMessage.observable bindTo { Log.e("TEST", it) }
    }

    override fun providePresentationModel(): MainPresentationModel = MainPresentationModel(client)
}
