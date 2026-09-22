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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentCondition
import com.example.data.model.DailyForecastItem
import com.example.data.model.SpeedUnit
import com.example.data.model.TemperatureUnit

@Composable
fun WeatherHeroCard(
    current: CurrentCondition,
    todayForecast: DailyForecastItem?,
    temperatureUnit: TemperatureUnit,
    speedUnit: SpeedUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Weather Icon with soft glowing aura
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = current.weatherType.icon,
                contentDescription = current.weatherType.description,
                tint = Color.White,
                modifier = Modifier.size(62.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Big Main Temperature
        Text(
            text = temperatureUnit.format(current.tempC),
            fontSize = 76.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            lineHeight = 78.sp,
            modifier = Modifier.testTag("hero_temperature_text")
        )

        // Weather Condition Description Pill
        Surface(
            color = Color.White.copy(alpha = 0.22f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        ) {
            Text(
                text = current.weatherType.description,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // High / Low & Feels Like Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (todayForecast != null) {
                Text(
                    text = "H: ${temperatureUnit.format(todayForecast.maxTempC)}  ·  L: ${temperatureUnit.format(todayForecast.minTempC)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = "  |  ",
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Light
                )
            }
            Text(
                text = "Feels like ${temperatureUnit.format(current.feelsLikeC)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Highlights Bar: Humidity, Wind, UV
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.16f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickMetricItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "${current.humidity}%"
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.25f))
                )
                QuickMetricItem(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "${speedUnit.format(current.windSpeedKmh)} ${current.windDirectionCardinal}"
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.25f))
                )
                QuickMetricItem(
                    icon = Icons.Default.WbSunny,
                    label = "UV Index",
                    value = "${Math.round(current.uvIndex * 10) / 10.0} (${current.uvLevelDescription})"
                )
            }
        }
    }
}

@Composable
private fun QuickMetricItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            )
        }
    }
}
