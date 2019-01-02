package io.samborskii.nusbus.api

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.net.HttpURLConnection

abstract class BaseMockWebServerTest {

    protected val server: MockWebServer = MockWebServer().apply {
        setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = response(request)
        })
    }

    @BeforeEach
    fun start() = server.start()

    @AfterEach
    fun stop() = server.shutdown()

    protected fun resolve(path: String): String = server.url(path).toString()

    protected fun notFound(): MockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)

    @Throws(InterruptedException::class)
    protected abstract fun response(request: RecordedRequest): MockResponse
}
