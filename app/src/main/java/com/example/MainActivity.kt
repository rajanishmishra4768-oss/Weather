package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.local.WeatherDatabase
import com.example.data.remote.NetworkClient
import com.example.data.repository.WeatherRepository
import com.example.ui.WeatherScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels {
        val database = WeatherDatabase.getInstance(applicationContext)
        val repository = WeatherRepository(
            apiService = NetworkClient.apiService,
            weatherDao = database.weatherDao()
        )
        WeatherViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherScreen(viewModel = weatherViewModel)
                }
            }
        }
    }
}
