package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.LocationItem
import com.example.data.model.SpeedUnit
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherInfo
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val errorMessage: String? = null,
    val selectedLocation: LocationItem,
    val favoriteLocations: List<LocationItem> = emptyList(),
    val isSelectedFavorite: Boolean = false,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val speedUnit: SpeedUnit = SpeedUnit.KMH,
    val searchQuery: String = "",
    val searchResults: List<LocationItem> = emptyList(),
    val isSearching: Boolean = false,
    val showLocationPicker: Boolean = false,
    val popularLocations: List<LocationItem> = emptyList()
)

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val defaultLocation = repository.popularWorldwideLocations.first()

    private val _uiState = MutableStateFlow(
        WeatherUiState(
            selectedLocation = defaultLocation,
            popularLocations = repository.popularWorldwideLocations
        )
    )
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var loadWeatherJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureDefaultFavorites()
        }

        viewModelScope.launch {
            repository.getFavoriteLocations().collect { favorites ->
                _uiState.update { state ->
                    val isFav = favorites.any { it.id == state.selectedLocation.id || (it.name == state.selectedLocation.name && it.countryCode == state.selectedLocation.countryCode) }
                    state.copy(
                        favoriteLocations = favorites,
                        isSelectedFavorite = isFav
                    )
                }
            }
        }

        loadWeather(defaultLocation)
    }

    fun selectLocation(location: LocationItem) {
        val isFav = _uiState.value.favoriteLocations.any { it.id == location.id || (it.name == location.name && it.countryCode == location.countryCode) }
        _uiState.update {
            it.copy(
                selectedLocation = location,
                isSelectedFavorite = isFav,
                showLocationPicker = false,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
        loadWeather(location)
    }

    fun refreshWeather() {
        val currentLoc = _uiState.value.selectedLocation
        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        loadWeather(currentLoc, isRefresh = true)
    }

    private fun loadWeather(location: LocationItem, isRefresh: Boolean = false) {
        loadWeatherJob?.cancel()
        loadWeatherJob = viewModelScope.launch {
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            try {
                val weather = repository.getWeatherForecast(location)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        weatherInfo = weather,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.localizedMessage ?: "Failed to load weather forecast. Please check your connection."
                    )
                }
            }
        }
    }

    fun toggleFavorite(location: LocationItem) {
        viewModelScope.launch {
            val isCurrentlyFav = _uiState.value.favoriteLocations.any {
                it.id == location.id || (it.name == location.name && it.countryCode == location.countryCode)
            }
            if (isCurrentlyFav) {
                repository.removeFavorite(location.id)
            } else {
                repository.addFavorite(location)
            }
        }
    }

    fun toggleTemperatureUnit() {
        _uiState.update {
            val newUnit = if (it.temperatureUnit == TemperatureUnit.CELSIUS) {
                TemperatureUnit.FAHRENHEIT
            } else {
                TemperatureUnit.CELSIUS
            }
            it.copy(temperatureUnit = newUnit)
        }
    }

    fun toggleSpeedUnit() {
        _uiState.update {
            val newUnit = if (it.speedUnit == SpeedUnit.KMH) {
                SpeedUnit.MPH
            } else {
                SpeedUnit.KMH
            }
            it.copy(speedUnit = newUnit)
        }
    }

    fun setLocationPickerVisible(visible: Boolean) {
        _uiState.update {
            it.copy(
                showLocationPicker = visible,
                searchQuery = if (!visible) "" else it.searchQuery,
                searchResults = if (!visible) emptyList() else it.searchResults
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // debounce typing
            _uiState.update { it.copy(isSearching = true) }
            val results = repository.searchLocations(query)
            _uiState.update {
                it.copy(
                    searchResults = results,
                    isSearching = false
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false
            )
        }
    }

    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
