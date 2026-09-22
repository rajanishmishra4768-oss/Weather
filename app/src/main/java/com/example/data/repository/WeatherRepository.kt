package com.example.data.repository

import com.example.data.local.FavoriteLocationEntity
import com.example.data.local.WeatherDao
import com.example.data.model.CurrentCondition
import com.example.data.model.DailyForecastItem
import com.example.data.model.HourlyForecastItem
import com.example.data.model.LocationItem
import com.example.data.model.WeatherApiResponse
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherType
import com.example.data.remote.OpenMeteoApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherRepository(
    private val apiService: OpenMeteoApiService,
    private val weatherDao: WeatherDao
) {

    val popularWorldwideLocations: List<LocationItem> = listOf(
        LocationItem(
            id = "tokyo_jp",
            name = "Tokyo",
            country = "Japan",
            countryCode = "JP",
            admin1 = "Tokyo",
            latitude = 35.6762,
            longitude = 139.6503,
            timezone = "Asia/Tokyo"
        ),
        LocationItem(
            id = "paris_fr",
            name = "Paris",
            country = "France",
            countryCode = "FR",
            admin1 = "Île-de-France",
            latitude = 48.8566,
            longitude = 2.3522,
            timezone = "Europe/Paris"
        ),
        LocationItem(
            id = "newyork_us",
            name = "New York",
            country = "United States",
            countryCode = "US",
            admin1 = "New York",
            latitude = 40.7128,
            longitude = -74.0060,
            timezone = "America/New_York"
        ),
        LocationItem(
            id = "london_gb",
            name = "London",
            country = "United Kingdom",
            countryCode = "GB",
            admin1 = "England",
            latitude = 51.5074,
            longitude = -0.1278,
            timezone = "Europe/London"
        ),
        LocationItem(
            id = "sydney_au",
            name = "Sydney",
            country = "Australia",
            countryCode = "AU",
            admin1 = "New South Wales",
            latitude = -33.8688,
            longitude = 151.2093,
            timezone = "Australia/Sydney"
        ),
        LocationItem(
            id = "dubai_ae",
            name = "Dubai",
            country = "United Arab Emirates",
            countryCode = "AE",
            admin1 = "Dubai",
            latitude = 25.2048,
            longitude = 55.2708,
            timezone = "Asia/Dubai"
        ),
        LocationItem(
            id = "cairo_eg",
            name = "Cairo",
            country = "Egypt",
            countryCode = "EG",
            admin1 = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357,
            timezone = "Africa/Cairo"
        ),
        LocationItem(
            id = "mumbai_in",
            name = "Mumbai",
            country = "India",
            countryCode = "IN",
            admin1 = "Maharashtra",
            latitude = 19.0760,
            longitude = 72.8777,
            timezone = "Asia/Kolkata"
        ),
        LocationItem(
            id = "riodejaneiro_br",
            name = "Rio de Janeiro",
            country = "Brazil",
            countryCode = "BR",
            admin1 = "Rio de Janeiro",
            latitude = -22.9068,
            longitude = -43.1729,
            timezone = "America/Sao_Paulo"
        ),
        LocationItem(
            id = "toronto_ca",
            name = "Toronto",
            country = "Canada",
            countryCode = "CA",
            admin1 = "Ontario",
            latitude = 43.6532,
            longitude = -79.3832,
            timezone = "America/Toronto"
        ),
        LocationItem(
            id = "singapore_sg",
            name = "Singapore",
            country = "Singapore",
            countryCode = "SG",
            admin1 = "Singapore",
            latitude = 1.3521,
            longitude = 103.8198,
            timezone = "Asia/Singapore"
        ),
        LocationItem(
            id = "rome_it",
            name = "Rome",
            country = "Italy",
            countryCode = "IT",
            admin1 = "Lazio",
            latitude = 41.9028,
            longitude = 12.4964,
            timezone = "Europe/Rome"
        )
    )

    suspend fun ensureDefaultFavorites() {
        val count = weatherDao.getFavoritesCount()
        if (count == 0) {
            val initialFavorites = popularWorldwideLocations.take(4).mapIndexed { index, loc ->
                FavoriteLocationEntity.fromLocationItem(
                    item = loc,
                    orderIndex = index,
                    isDefault = (index == 0)
                )
            }
            weatherDao.insertFavorites(initialFavorites)
        }
    }

    fun getFavoriteLocations(): Flow<List<LocationItem>> {
        return weatherDao.getAllFavorites().map { entities ->
            entities.map { it.toLocationItem() }
        }
    }

    fun isFavorite(locationId: String): Flow<Boolean> {
        return weatherDao.isFavorite(locationId)
    }

    suspend fun addFavorite(location: LocationItem) {
        val entity = FavoriteLocationEntity.fromLocationItem(
            item = location.copy(isFavorite = true),
            orderIndex = 0
        )
        weatherDao.insertFavorite(entity)
    }

    suspend fun removeFavorite(locationId: String) {
        weatherDao.deleteFavoriteById(locationId)
    }

    suspend fun searchLocations(query: String): List<LocationItem> {
        if (query.trim().length < 2) return emptyList()
        return try {
            val response = apiService.searchLocations(name = query.trim())
            response.results.orEmpty().map { dto ->
                val id = "${dto.latitude}_${dto.longitude}"
                LocationItem(
                    id = id,
                    name = dto.name,
                    country = dto.country ?: "",
                    countryCode = dto.countryCode ?: "",
                    admin1 = dto.admin1,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    timezone = dto.timezone
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWeatherForecast(location: LocationItem): WeatherInfo {
        val response = apiService.getForecast(
            latitude = location.latitude,
            longitude = location.longitude
        )
        return mapToWeatherInfo(location, response)
    }

    private fun mapToWeatherInfo(location: LocationItem, response: WeatherApiResponse): WeatherInfo {
        val currentDto = response.current ?: throw IllegalStateException("Current weather unavailable")
        val isDay = (currentDto.isDay ?: 1) == 1
        val weatherCode = currentDto.weatherCode ?: 0
        val weatherType = WeatherType.fromWmoCode(weatherCode, isDay)

        val current = CurrentCondition(
            tempC = currentDto.temperature ?: 0.0,
            feelsLikeC = currentDto.apparentTemperature ?: currentDto.temperature ?: 0.0,
            humidity = currentDto.relativeHumidity ?: 0,
            windSpeedKmh = currentDto.windSpeed ?: 0.0,
            windDirectionDeg = currentDto.windDirection ?: 0.0,
            uvIndex = currentDto.uvIndex ?: 0.0,
            precipitationMm = currentDto.precipitation ?: 0.0,
            pressureHpa = currentDto.pressureMsl ?: 1013.2,
            cloudCover = currentDto.cloudCover ?: 0,
            isDay = isDay,
            weatherType = weatherType,
            timeIso = currentDto.time ?: ""
        )

        // Parse Hourly
        val hourlyDto = response.hourly
        val hourlyTimes = hourlyDto?.time.orEmpty()
        val hourlyTemps = hourlyDto?.temperature.orEmpty()
        val hourlyPrecip = hourlyDto?.precipitationProbability.orEmpty()
        val hourlyCodes = hourlyDto?.weatherCode.orEmpty()

        // Find starting index close to current hour
        val currentHourPrefix = currentDto.time?.take(13) // e.g. "2026-09-22T14"
        val startIndex = if (currentHourPrefix != null) {
            val idx = hourlyTimes.indexOfFirst { it.startsWith(currentHourPrefix) }
            if (idx >= 0) idx else 0
        } else 0

        val maxHourlyItems = 24
        val hourlyItems = mutableListOf<HourlyForecastItem>()
        for (i in startIndex until minOf(hourlyTimes.size, startIndex + maxHourlyItems)) {
            val timeStr = hourlyTimes[i]
            val temp = hourlyTemps.getOrNull(i) ?: 0.0
            val precipProb = hourlyPrecip.getOrNull(i) ?: 0
            val code = hourlyCodes.getOrNull(i) ?: 0

            val hourInt = timeStr.substringAfter("T", "").substringBefore(":", "").toIntOrNull() ?: 12
            val isHourDay = hourInt in 6..19
            val itemType = WeatherType.fromWmoCode(code, isHourDay)
            val isNow = (i == startIndex)

            val timeLabel = if (isNow) "Now" else formatHourLabel(hourInt)

            hourlyItems.add(
                HourlyForecastItem(
                    timeLabel = timeLabel,
                    tempC = temp,
                    precipitationProb = precipProb,
                    weatherType = itemType,
                    isNow = isNow
                )
            )
        }

        // Parse Daily (7-day forecast)
        val dailyDto = response.daily
        val dailyTimes = dailyDto?.time.orEmpty()
        val dailyCodes = dailyDto?.weatherCode.orEmpty()
        val dailyMaxTemps = dailyDto?.temperatureMax.orEmpty()
        val dailyMinTemps = dailyDto?.temperatureMin.orEmpty()
        val dailyPrecipProb = dailyDto?.precipitationProbabilityMax.orEmpty()
        val dailyUv = dailyDto?.uvIndexMax.orEmpty()
        val dailySunrise = dailyDto?.sunrise.orEmpty()
        val dailySunset = dailyDto?.sunset.orEmpty()

        val dailyItems = mutableListOf<DailyForecastItem>()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        for (i in dailyTimes.indices) {
            val dateStr = dailyTimes[i]
            val code = dailyCodes.getOrNull(i) ?: 0
            val itemType = WeatherType.fromWmoCode(code, isDay = true)
            val maxT = dailyMaxTemps.getOrNull(i) ?: 0.0
            val minT = dailyMinTemps.getOrNull(i) ?: 0.0
            val pProb = dailyPrecipProb.getOrNull(i) ?: 0
            val uv = dailyUv.getOrNull(i) ?: 0.0
            val sunriseTime = dailySunrise.getOrNull(i)?.substringAfter("T") ?: "--:--"
            val sunsetTime = dailySunset.getOrNull(i)?.substringAfter("T") ?: "--:--"

            val dayLabel = when {
                dateStr == todayStr || i == 0 -> "Today"
                i == 1 -> "Tomorrow"
                else -> formatDayOfWeek(dateStr)
            }
            val dateLabel = formatDateLabel(dateStr)

            dailyItems.add(
                DailyForecastItem(
                    dayLabel = dayLabel,
                    dateLabel = dateLabel,
                    weatherType = itemType,
                    minTempC = minT,
                    maxTempC = maxT,
                    precipitationProb = pProb,
                    uvIndexMax = uv,
                    sunrise = sunriseTime,
                    sunset = sunsetTime
                )
            )
        }

        return WeatherInfo(
            location = location,
            current = current,
            hourly = hourlyItems,
            daily = dailyItems,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    private fun formatHourLabel(hour: Int): String {
        return when {
            hour == 0 -> "12 AM"
            hour < 12 -> "$hour AM"
            hour == 12 -> "12 PM"
            else -> "${hour - 12} PM"
        }
    }

    private fun formatDayOfWeek(dateStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateStr) ?: return dateStr
            SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun formatDateLabel(dateStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateStr) ?: return dateStr
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            dateStr
        }
    }
}
