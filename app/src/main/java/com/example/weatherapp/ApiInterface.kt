import com.example.weatherapp.WeatherApp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String,

    ): Call<WeatherApp>

    companion object {
    fun getWeatherData(city: String, appId: String, units: String): Any {
        TODO("Not yet implemented")
    }
}
}