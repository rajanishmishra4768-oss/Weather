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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecastItem
import com.example.data.model.TemperatureUnit

@Composable
fun DailyForecastSection(
    dailyItems: List<DailyForecastItem>,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    // Calculate overall min & max across the week for relative temperature bars
    val weekMinTemp = dailyItems.minOfOrNull { it.minTempC } ?: 0.0
    val weekMaxTemp = dailyItems.maxOfOrNull { it.maxTempC } ?: 30.0
    val tempSpan = (weekMaxTemp - weekMinTemp).coerceAtLeast(1.0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.16f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "7-DAY FORECAST",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                )
            }

            // Daily Rows
            dailyItems.forEachIndexed { index, item ->
                DailyForecastRow(
                    item = item,
                    temperatureUnit = temperatureUnit,
                    weekMinTemp = weekMinTemp,
                    tempSpan = tempSpan
                )
                if (index < dailyItems.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyForecastRow(
    item: DailyForecastItem,
    temperatureUnit: TemperatureUnit,
    weekMinTemp: Double,
    tempSpan: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day Label & Date
        Column(modifier = Modifier.width(88.dp)) {
            Text(
                text = item.dayLabel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (item.dayLabel == "Today") FontWeight.Bold else FontWeight.Medium,
                    color = Color.White
                )
            )
            Text(
                text = item.dateLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )
        }

        // Weather Icon + Rain Prob
        Row(
            modifier = Modifier.width(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = item.weatherType.icon,
                contentDescription = item.weatherType.description,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            if (item.precipitationProb > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF67E8F9),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${item.precipitationProb}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF67E8F9),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Min Temp
        Text(
            text = temperatureUnit.format(item.minTempC),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.End
            ),
            modifier = Modifier.width(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Visual Temperature Range Slider Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.CenterStart
        ) {
            val startRatio = ((item.minTempC - weekMinTemp) / tempSpan).toFloat().coerceIn(0f, 1f)
            val endRatio = ((item.maxTempC - weekMinTemp) / tempSpan).toFloat().coerceIn(startRatio, 1f)
            val barWidthFraction = (endRatio - startRatio).coerceAtLeast(0.08f)

            Row(modifier = Modifier.fillMaxWidth()) {
                if (startRatio > 0.01f) {
                    Spacer(modifier = Modifier.weight(startRatio.coerceAtLeast(0.001f)))
                }
                Box(
                    modifier = Modifier
                        .weight(barWidthFraction)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF38BDF8), Color(0xFFFBBF24), Color(0xFFFB923C))
                            )
                        )
                )
                val remaining = (1f - endRatio).coerceAtLeast(0f)
                if (remaining > 0.01f) {
                    Spacer(modifier = Modifier.weight(remaining.coerceAtLeast(0.001f)))
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Max Temp
        Text(
            text = temperatureUnit.format(item.maxTempC),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.width(40.dp)
        )
    }
}
