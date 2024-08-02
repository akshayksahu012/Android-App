package com.example.weatherapp

import ApiInterface
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.let as let


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val apiKey = "d212005aee51dd09e215fabfe4f5716a" // Replace with your actual API key

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        SearchCity()
    }


    private fun SearchCity() {
        val searchView = binding.searchView2
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let()
                {
                    fetchWeatherData(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You might want to implement filtering or suggestions here
                return true
            }
        })
    }


    @SuppressLint("SetTextLocale")
    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        val units = "metric"

        apiInterface.getWeatherData(cityName, apiKey, units).enqueue(object : Callback<WeatherApp> {

            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString() + " ℃"
                    val humidity = responseBody.main.humidity.toString() + " %"
                    val windSpeed = responseBody.wind.speed.toString() + " m/s"
                    val sunrise = formatTime(responseBody.sys.sunrise * 1000L) // Convert to milliseconds
                    val sunset = formatTime(responseBody.sys.sunset * 1000L)
                    val seaLevel = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max.toString() + " ℃"
                    val minTemp = responseBody.main.temp_min.toString() + " ℃"

                    binding.temp.text = temperature
                    val condition = null
                    binding.weather.text = condition // Assuming you have a condition variable based on weather data
                    binding.maxtemp.text = "Max Temp: $maxTemp"
                    binding.mintemp.text = "Min Temp: $minTemp"
                    binding.humidity.text = humidity
                    binding.wind.text = windSpeed
                    binding.sunrise.text = sunrise
                    binding.sunset.text = sunset
                    binding.condition.text = condition // Assuming you have a condition variable based on weather data
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityname.text = cityName

                    changeImagesWeatherCondition(condition.toString())
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                // Handle network errors
                Log.e("TAG", "onFailure: Error fetching weather data", t)
            }
        })
    }


    private fun changeImagesWeatherCondition(conditions: String) {
        val drawableId = when (conditions) {
            "Partly Clouds", "Clouds","Overcast","Misty","Foggy" -> R.drawable.colud_background
            "Clear Sky", "Sunny", "Clear" -> R.drawable.sunny_background
                "Light Rain", "Drizzle","Moderate Rain","Showers" ,"Heavy Rain"-> R.drawable.rain_background
                "Light Snow","Moderate","Heavy Snow","Blizzard" -> R.drawable.snow_background
            else -> R.drawable.sunny_background // Default background for unknown conditions
        }
        binding.root.setBackgroundResource(drawableId)

        val animationId = when (conditions) {
            "Partly Clouds", "Clouds","Overcast","Misty","Foggy" -> R.raw.cloud
            "Clear Sky", "Sunny", "Clear" -> R.raw.sun
                "Light Rain", "Drizzle","Moderate Rain","Showers" ,"Heavy Rain" -> R.raw.rain
                "Light Snow","Moderate","Heavy Snow","Blizzard" -> R.raw.snow
            // Add more conditions and corresponding animation resources here
            else -> 0 // No animation for unknown conditions (optional)
        }
        binding.lottieAnimationView.setAnimation(animationId)

        binding.lottieAnimationView.playAnimation()
    }



    private fun formatTime(timestamp: Long): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(Date(timestamp))
    }

    private fun date(): String {
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("dd MMMM yyyy", locale)
        return sdf.format(Date())
    }


    fun dayName(timestamp: Long): String {
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("EEEE", locale) // "EEEE" for the full day name
        return sdf.format(Date(timestamp))
    }
}
