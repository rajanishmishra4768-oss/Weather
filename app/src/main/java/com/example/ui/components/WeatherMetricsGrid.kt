package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentCondition
import com.example.data.model.DailyForecastItem
import com.example.data.model.SpeedUnit

@Composable
fun WeatherMetricsGrid(
    current: CurrentCondition,
    todayForecast: DailyForecastItem?,
    speedUnit: SpeedUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Wind & Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Default.Air,
                title = "WIND",
                value = speedUnit.format(current.windSpeedKmh),
                description = "Direction: ${current.windDirectionCardinal} (${current.windDirectionDeg.toInt()}°)",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Default.WaterDrop,
                title = "HUMIDITY",
                value = "${current.humidity}%",
                description = when {
                    current.humidity < 30 -> "Dry and crisp air"
                    current.humidity < 60 -> "Comfortable moisture"
                    current.humidity < 80 -> "Humid and muggy"
                    else -> "Very high humidity"
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: UV Index & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Default.WbSunny,
                title = "UV INDEX",
                value = "${Math.round(current.uvIndex * 10) / 10.0}",
                description = "${current.uvLevelDescription} exposure level",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Default.Compress,
                title = "PRESSURE",
                value = "${current.pressureHpa.toInt()} hPa",
                description = when {
                    current.pressureHpa > 1015 -> "High pressure (stable)"
                    current.pressureHpa < 1005 -> "Low pressure (stormy)"
                    else -> "Normal sea level"
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Cloud Cover & Sunrise / Sunset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Default.FilterDrama,
                title = "CLOUD COVER",
                value = "${current.cloudCover}%",
                description = when {
                    current.cloudCover < 20 -> "Mostly sunny"
                    current.cloudCover < 60 -> "Scattered clouds"
                    else -> "Cloudy conditions"
                },
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Default.WbTwilight,
                title = "SUN SCHEDULE",
                value = todayForecast?.sunrise ?: "--:--",
                description = "Sunset: ${todayForecast?.sunset ?: "--:--"}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.16f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 0.8.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                ),
                maxLines = 2
            )
        }
    }
}
