package jp.hotdrop.comlis.repository

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.BufferedReader
import java.io.InputStreamReader

class MockComlisServer {

    private val mockWebServer = MockWebServer()

    fun startNormal() {
        mockWebServer.setDispatcher(dispatcher)
        mockWebServer.start()
    }

    fun stop() {
        mockWebServer.shutdown()
    }

    fun getUrl(): HttpUrl = mockWebServer.url("/")

    private val dispatcher = (object: Dispatcher() {
        override fun dispatch(request: RecordedRequest?): MockResponse {

            if(request == null || request.path == null) {
                return MockResponse().setResponseCode(400)
            }

            val regex = Regex("/companies\\?fromDateEpoch=\\d+")

            return if(regex.matches(request.path)) {
                MockResponse().setBody(readJson("test_companies.json")).setResponseCode(200)
            } else {
                MockResponse().setResponseCode(404)
            }
        }
    })

    private fun readJson(jsonFileName: String): String {
        val stringBuilder = StringBuilder()
        val inputStream = javaClass.classLoader.getResourceAsStream(jsonFileName)
        BufferedReader(InputStreamReader(inputStream)).use {
            it.lineSequence().iterator().forEach { stringBuilder.append(it) }
        }
        return stringBuilder.toString()
    }
}