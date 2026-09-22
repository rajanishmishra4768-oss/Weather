package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WeatherType
import com.example.ui.components.DailyForecastSection
import com.example.ui.components.HourlyForecastSection
import com.example.ui.components.LocationPickerDialog
import com.example.ui.components.QuickLocationSelector
import com.example.ui.components.WeatherHeroCard
import com.example.ui.components.WeatherMetricsGrid
import com.example.ui.components.WeatherTopBar
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentType = uiState.weatherInfo?.current?.weatherType
    val gradientColors = currentType?.gradientColors ?: listOf(
        Color(0xFF0284C7),
        Color(0xFF0369A1),
        Color(0xFF0C4A6E)
    )

    // Animated dynamic weather gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(gradientColors)
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Navigation & Location Header
            WeatherTopBar(
                location = uiState.selectedLocation,
                isFavorite = uiState.isSelectedFavorite,
                temperatureUnit = uiState.temperatureUnit,
                onLocationClick = { viewModel.setLocationPickerVisible(true) },
                onFavoriteClick = { viewModel.toggleFavorite(uiState.selectedLocation) },
                onUnitToggle = { viewModel.toggleTemperatureUnit() },
                onRefreshClick = { viewModel.refreshWeather() }
            )

            // Quick Worldwide Location Chips
            QuickLocationSelector(
                selectedLocation = uiState.selectedLocation,
                favoriteLocations = uiState.favoriteLocations,
                popularLocations = uiState.popularLocations,
                onSelectLocation = { viewModel.selectLocation(it) },
                onAddLocationClick = { viewModel.setLocationPickerVisible(true) },
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Main Content Area
            if (uiState.isLoading && uiState.weatherInfo == null) {
                // Initial Loading State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Loading forecast for ${uiState.selectedLocation.name}...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            } else if (uiState.errorMessage != null && uiState.weatherInfo == null) {
                // Error State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(54.dp)
                            )
                            Text(
                                text = "Unable to Load Forecast",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = uiState.errorMessage ?: "Please verify internet connection and try again.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.refreshWeather() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("retry_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Retry")
                            }
                        }
                    }
                }
            } else {
                // Weather Data Display
                val weather = uiState.weatherInfo
                if (weather != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .testTag("weather_content_column"),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Current Weather Hero
                        item {
                            WeatherHeroCard(
                                current = weather.current,
                                todayForecast = weather.daily.firstOrNull(),
                                temperatureUnit = uiState.temperatureUnit,
                                speedUnit = uiState.speedUnit
                            )
                        }

                        // Hourly 24-Hour Forecast
                        item {
                            HourlyForecastSection(
                                hourlyItems = weather.hourly,
                                temperatureUnit = uiState.temperatureUnit
                            )
                        }

                        // 7-Day Forecast
                        item {
                            DailyForecastSection(
                                dailyItems = weather.daily,
                                temperatureUnit = uiState.temperatureUnit
                            )
                        }

                        // Weather Metric Details Grid
                        item {
                            WeatherMetricsGrid(
                                current = weather.current,
                                todayForecast = weather.daily.firstOrNull(),
                                speedUnit = uiState.speedUnit
                            )
                        }

                        // Safe padding item at bottom
                        item {
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                }
            }
        }

        // Global Location Picker Modal Dialog
        LocationPickerDialog(
            visible = uiState.showLocationPicker,
            searchQuery = uiState.searchQuery,
            searchResults = uiState.searchResults,
            isSearching = uiState.isSearching,
            favoriteLocations = uiState.favoriteLocations,
            popularLocations = uiState.popularLocations,
            onQueryChanged = { viewModel.onSearchQueryChanged(it) },
            onClearQuery = { viewModel.clearSearch() },
            onSelectLocation = { viewModel.selectLocation(it) },
            onDeleteFavorite = { viewModel.toggleFavorite(com.example.data.model.LocationItem(id = it, name = "", country = "", countryCode = "", latitude = 0.0, longitude = 0.0)) },
            onDismiss = { viewModel.setLocationPickerVisible(false) }
        )
    }
}
