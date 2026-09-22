package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    fun format(tempCelsius: Double): String {
        val converted = if (this == FAHRENHEIT) (tempCelsius * 9.0 / 5.0) + 32.0 else tempCelsius
        return "${Math.round(converted)}$symbol"
    }

    fun value(tempCelsius: Double): Int {
        val converted = if (this == FAHRENHEIT) (tempCelsius * 9.0 / 5.0) + 32.0 else tempCelsius
        return Math.round(converted).toInt()
    }
}

enum class SpeedUnit(val symbol: String) {
    KMH("km/h"),
    MPH("mph");

    fun format(speedKmh: Double): String {
        val converted = if (this == MPH) speedKmh * 0.621371 else speedKmh
        return "${Math.round(converted * 10) / 10.0} $symbol"
    }
}

data class LocationItem(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = if (!admin1.isNullOrBlank() && admin1 != name) "$name, $admin1" else name

    val flagEmoji: String
        get() = CountryFlagUtil.getFlagEmoji(countryCode)
}

enum class WeatherConditionCategory {
    CLEAR_DAY,
    CLEAR_NIGHT,
    CLOUDY,
    OVERCAST,
    FOGGY,
    RAINY,
    THUNDERSTORM,
    SNOWY,
    WINDY
}

data class WeatherType(
    val code: Int,
    val description: String,
    val category: WeatherConditionCategory,
    val icon: ImageVector,
    val gradientColors: List<Color>
) {
    companion object {
        fun fromWmoCode(code: Int, isDay: Boolean = true): WeatherType {
            return when (code) {
                0 -> if (isDay) {
                    WeatherType(
                        code,
                        "Clear Sky",
                        WeatherConditionCategory.CLEAR_DAY,
                        Icons.Default.WbSunny,
                        listOf(Color(0xFF1E88E5), Color(0xFF42A5F5), Color(0xFFFFB74D))
                    )
                } else {
                    WeatherType(
                        code,
                        "Clear Night",
                        WeatherConditionCategory.CLEAR_NIGHT,
                        Icons.Default.NightsStay,
                        listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77))
                    )
                }
                1 -> if (isDay) {
                    WeatherType(
                        code,
                        "Mainly Clear",
                        WeatherConditionCategory.CLEAR_DAY,
                        Icons.Default.WbSunny,
                        listOf(Color(0xFF1976D2), Color(0xFF64B5F6), Color(0xFFFFCC80))
                    )
                } else {
                    WeatherType(
                        code,
                        "Mainly Clear",
                        WeatherConditionCategory.CLEAR_NIGHT,
                        Icons.Default.NightsStay,
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                    )
                }
                2 -> WeatherType(
                    code,
                    "Partly Cloudy",
                    WeatherConditionCategory.CLOUDY,
                    Icons.Default.WbCloudy,
                    if (isDay) listOf(Color(0xFF2B5876), Color(0xFF4E4376)) else listOf(Color(0xFF141E30), Color(0xFF243B55))
                )
                3 -> WeatherType(
                    code,
                    "Overcast",
                    WeatherConditionCategory.OVERCAST,
                    Icons.Default.Cloud,
                    listOf(Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF78909C))
                )
                45, 48 -> WeatherType(
                    code,
                    "Foggy",
                    WeatherConditionCategory.FOGGY,
                    Icons.Default.Air,
                    listOf(Color(0xFF455A64), Color(0xFF607D8B), Color(0xFF90A4AE))
                )
                51, 53, 55 -> WeatherType(
                    code,
                    "Drizzle",
                    WeatherConditionCategory.RAINY,
                    Icons.Default.Grain,
                    listOf(Color(0xFF1A365D), Color(0xFF2B6CB0), Color(0xFF4299E1))
                )
                56, 57 -> WeatherType(
                    code,
                    "Freezing Drizzle",
                    WeatherConditionCategory.SNOWY,
                    Icons.Default.Grain,
                    listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF))
                )
                61, 63, 65 -> WeatherType(
                    code,
                    when (code) {
                        61 -> "Light Rain"
                        63 -> "Moderate Rain"
                        else -> "Heavy Rain"
                    },
                    WeatherConditionCategory.RAINY,
                    Icons.Default.WaterDrop,
                    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
                66, 67 -> WeatherType(
                    code,
                    "Freezing Rain",
                    WeatherConditionCategory.SNOWY,
                    Icons.Default.Grain,
                    listOf(Color(0xFF1F4068), Color(0xFF162447))
                )
                71, 73, 75, 77 -> WeatherType(
                    code,
                    when (code) {
                        71 -> "Light Snow"
                        73 -> "Moderate Snow"
                        75 -> "Heavy Snow"
                        else -> "Snow Grains"
                    },
                    WeatherConditionCategory.SNOWY,
                    Icons.Default.Grain,
                    listOf(Color(0xFF4B6CB7), Color(0xFF182848))
                )
                80, 81, 82 -> WeatherType(
                    code,
                    "Rain Showers",
                    WeatherConditionCategory.RAINY,
                    Icons.Default.WaterDrop,
                    listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
                )
                85, 86 -> WeatherType(
                    code,
                    "Snow Showers",
                    WeatherConditionCategory.SNOWY,
                    Icons.Default.Grain,
                    listOf(Color(0xFF3A6073), Color(0xFF3A7BD5))
                )
                95 -> WeatherType(
                    code,
                    "Thunderstorm",
                    WeatherConditionCategory.THUNDERSTORM,
                    Icons.Default.Thunderstorm,
                    listOf(Color(0xFF141E30), Color(0xFF243B55), Color(0xFF4A148C))
                )
                96, 99 -> WeatherType(
                    code,
                    "Severe Thunderstorm",
                    WeatherConditionCategory.THUNDERSTORM,
                    Icons.Default.Thunderstorm,
                    listOf(Color(0xFF000000), Color(0xFF240B36), Color(0xFFC31432))
                )
                else -> WeatherType(
                    code,
                    "Clear",
                    WeatherConditionCategory.CLEAR_DAY,
                    Icons.Default.WbSunny,
                    listOf(Color(0xFF1E88E5), Color(0xFF42A5F5))
                )
            }
        }
    }
}

data class CurrentCondition(
    val tempC: Double,
    val feelsLikeC: Double,
    val humidity: Int,
    val windSpeedKmh: Double,
    val windDirectionDeg: Double,
    val uvIndex: Double,
    val precipitationMm: Double,
    val pressureHpa: Double,
    val cloudCover: Int,
    val isDay: Boolean,
    val weatherType: WeatherType,
    val timeIso: String
) {
    val windDirectionCardinal: String
        get() {
            val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
            val index = Math.round((windDirectionDeg % 360) / 22.5).toInt() % 16
            return directions[index]
        }

    val uvLevelDescription: String
        get() = when {
            uvIndex < 3.0 -> "Low"
            uvIndex < 6.0 -> "Moderate"
            uvIndex < 8.0 -> "High"
            uvIndex < 11.0 -> "Very High"
            else -> "Extreme"
        }
}

data class HourlyForecastItem(
    val timeLabel: String,
    val tempC: Double,
    val precipitationProb: Int,
    val weatherType: WeatherType,
    val isNow: Boolean = false
)

data class DailyForecastItem(
    val dayLabel: String,
    val dateLabel: String,
    val weatherType: WeatherType,
    val minTempC: Double,
    val maxTempC: Double,
    val precipitationProb: Int,
    val uvIndexMax: Double,
    val sunrise: String,
    val sunset: String
)

data class WeatherInfo(
    val location: LocationItem,
    val current: CurrentCondition,
    val hourly: List<HourlyForecastItem>,
    val daily: List<DailyForecastItem>,
    val lastUpdatedMillis: Long
)
