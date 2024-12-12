package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

//20270d26809877b3f00d51ccd698d23e

class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
       val retrofit = Retrofit.Builder()
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .addConverterFactory(GsonConverterFactory.create())
           .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"20270d26809877b3f00d51ccd698d23e","metric")
        response.enqueue(object : Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null ){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text= "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.Condition.text = condition
                    binding.day.text= dayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.cityName.text="$cityName"
                  // Log.d("TAG", "onResponse: $temperature")

                    changeImagesAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e(TAG, "Error fetching weather data", t)
            }

        })

    }

    private fun changeImagesAccordingToWeatherCondition(conditions: String) {
       when (conditions){

           "Clear Sky", "Sunny", "Clear" ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }

           "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }

           "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
               binding.root.setBackgroundResource(R.drawable.rain_background)
               binding.lottieAnimationView.setAnimation(R.raw.rain)
           }

           "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieAnimationView.setAnimation(R.raw.snow)
           }
           else ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
       }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))

    }

    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))

    }

    fun dayName(timeStamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}