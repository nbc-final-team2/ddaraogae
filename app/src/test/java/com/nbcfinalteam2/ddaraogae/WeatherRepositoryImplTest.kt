package com.nbcfinalteam2.ddaraogae

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.RetrofitDataSource
import com.nbcfinalteam2.ddaraogae.data.dto.*
import com.nbcfinalteam2.ddaraogae.data.mapper.WeatherMapper
import com.nbcfinalteam2.ddaraogae.data.repository.WeatherRepositoryImpl
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WeatherRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var retrofitDataSource: RetrofitDataSource
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        retrofitDataSource = mock(RetrofitDataSource::class.java)
        weatherRepository = WeatherRepositoryImpl(retrofitDataSource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getWeatherData returns correct data`() = runBlocking {
        val lat = "126.98"
        val lon = "37.56"

        val weatherResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                {
                  "coord": {
                    "lon": 37.56,
                    "lat": 126.98
                  },
                  "weather": [
                    {
                      "id": 800,
                    }
                  ],
                  "main": {
                    "temp": 26.02
                  }
                }
            """.trimIndent()
            )

        val dustResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                {
                  "list": [
                    {
                      "components": {
                        "pm2_5": 10.0,
                        "pm10": 20.0
                      }
                    }
                  ]
                }
            """.trimIndent()
            )

        mockWebServer.enqueue(weatherResponse)
        mockWebServer.enqueue(dustResponse)

        `when`(retrofitDataSource.getWeather(lat, lon)).thenReturn(
            Weather(
                coord = WeatherCoord(lat = 37.57, lon = 126.98),
                weather = listOf(WeatherWeather(id = 800)),
                main = WeatherMain(temp = 23.0)
            )
        )
        `when`(retrofitDataSource.getDust(lat, lon)).thenReturn(
            Dust(
                list = listOf(
                    DustList(
                        components = DustComponents(pm25 = 10.0, pm10 = 20.0)
                    )
                )
            )
        )

        val expectedWeatherItem = WeatherMapper.toWeatherData(
            Weather(
                coord = WeatherCoord(lat = 37.57, lon = 126.98),
                weather = listOf(WeatherWeather(id = 800)),
                main = WeatherMain(temp = 23.0)
            ),
            Dust(
                list = listOf(
                    DustList(
                        components = DustComponents(pm25 = 10.0, pm10 = 20.0)
                    )
                )
            )
        )

        val actualWeatherItem = weatherRepository.getWeatherData(lat, lon)

        assertEquals(expectedWeatherItem, actualWeatherItem)
    }
}