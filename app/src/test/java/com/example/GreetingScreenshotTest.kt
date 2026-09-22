package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.CurrentCondition
import com.example.data.model.DailyForecastItem
import com.example.data.model.SpeedUnit
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherType
import com.example.ui.components.WeatherHeroCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val mockCurrent = CurrentCondition(
      tempC = 22.0,
      feelsLikeC = 21.5,
      humidity = 62,
      windSpeedKmh = 14.2,
      windDirectionDeg = 310.0,
      uvIndex = 4.5,
      precipitationMm = 0.0,
      pressureHpa = 1014.0,
      cloudCover = 20,
      isDay = true,
      weatherType = WeatherType.fromWmoCode(0, true),
      timeIso = "2026-09-22T12:00"
    )
    val mockDaily = DailyForecastItem(
      dayLabel = "Today",
      dateLabel = "Sep 22",
      weatherType = WeatherType.fromWmoCode(0, true),
      minTempC = 15.0,
      maxTempC = 25.0,
      precipitationProb = 0,
      uvIndexMax = 5.0,
      sunrise = "06:45",
      sunset = "19:10"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        WeatherHeroCard(
          current = mockCurrent,
          todayForecast = mockDaily,
          temperatureUnit = TemperatureUnit.CELSIUS,
          speedUnit = SpeedUnit.KMH
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
