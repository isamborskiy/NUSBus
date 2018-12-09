package io.samborskii.nusbus.ui.main

import android.os.Bundle
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.samborskii.nusbus.R
import io.samborskii.nusbus.api.impl.NusBusClientImpl
import me.dmdev.rxpm.base.PmSupportActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : PmSupportActivity<MainPresentationModel>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBindPresentationModel(pm: MainPresentationModel) {
        pm.data.observable bindTo { Log.i("TEST", "Size: ${it.size}") }

        pm.errorMessage.observable bindTo { Log.e("TEST", it) }
    }

    override fun providePresentationModel(): MainPresentationModel {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val mapper = jacksonObjectMapper()

        val client = NusBusClientImpl(okHttpClient, mapper)
        return MainPresentationModel(client)
    }
}
