package net.rishiz.weather.view.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import net.rishiz.weather.R
import net.rishiz.weather.SharedPrefs
import net.rishiz.weather.adapter.FutureForecastAdapter
import net.rishiz.weather.databinding.ActivityFutureForecastBinding
import net.rishiz.weather.model.WeatherList
import net.rishiz.weather.viewmodel.WeatherViewModel

class FutureForecastActivity : AppCompatActivity() {
    companion object {
        private val TAG = FutureForecastActivity::class.java.canonicalName
    }

    private val binding: ActivityFutureForecastBinding by lazy {
        ActivityFutureForecastBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: WeatherViewModel
    private lateinit var futureForecastAdapter: FutureForecastAdapter
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        futureForecastAdapter = FutureForecastAdapter()
        recyclerView = findViewById(R.id.recyclerView)
        val shardPref = SharedPrefs.getInstance(this)
        val city = shardPref.getValueOrNull("city")

        if (city != null) {
            viewModel.getFutureWeather(city)
        } else {
            Log.d("future", "on")
            viewModel.getFutureWeather()
        }

        viewModel.tomorrowWeatherData.observe(this) {

            if (it != null) {
                for (weather in it) {

                    val temp = viewModel.convertTempFaherenheitToCelsius(weather.main.temp)
                    val temp_min = viewModel.convertTempFaherenheitToCelsius(weather.main.temp_min)
                    val temp_max = viewModel.convertTempFaherenheitToCelsius(weather.main.temp_max)

                    "${temp}\u2103".also { binding.temp.text = it }
                    "${temp_min}\u2103".also { binding.minTemp.text = it }
                    "${temp_max}\u2103".also { binding.maxTemp.text = it }
                    "${weather.main.humidity}%".also { binding.humidity.text = it }
                    "${weather.wind.speed} m/s".also { binding.windspeed.text = it }
                    "${weather.pop}%".also { binding.chanceofrain.text = it }

                    for (i in weather.weather) {
                        val description = i.description
                        binding.weatherDscr.text = description
                        changeImagesAccordingToWeather(description)
                    }
                }
            }
        }

        viewModel.futurWeatherLiveDataList.observe(this) {
            val setWeatherList = it as List<WeatherList>
            Log.d("FutureForecastActivity", setWeatherList.toString())
            recyclerView.adapter = futureForecastAdapter
            futureForecastAdapter.setList(setWeatherList)

        }
    }

    private fun changeImagesAccordingToWeather(condition: String) {
        when (condition) {
            "clear sky" -> {
                binding.root.setBackgroundResource(R.drawable.clear_sky_bg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> {
                binding.root.setBackgroundResource(R.drawable.cloud_bg)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "rain", "light rain", "shower rain", "moderate rain", "light intensity shower rain", "ragged shower rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_bg)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_bg)
                binding.lottieAnimationView.setAnimation(R.raw.heavy_rain)
            }

            "thunderstorm", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm" -> {
                binding.root.setBackgroundResource(R.drawable.thunderstorm_bg)
                binding.lottieAnimationView.setAnimation(R.raw.thunderstorm)
            }

            "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain" -> {
                binding.root.setBackgroundResource(R.drawable.thunderstorm_bg)
                binding.lottieAnimationView.setAnimation(R.raw.thunderstorm_with_rain)
            }

            "light snow", "snow", "heavy snow", "light shower sleet", "sleet", "ight shower sleet", "light rain and snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_bg)
                binding.lottieAnimationView.setAnimation(R.raw.snow_weather)
            }

            "mist", "smoke", "dust", "fog", "sand", "haze", "dust whirls", "volcanic ash", "squalls" -> {
                binding.root.setBackgroundResource(R.drawable.mist_bg)
                binding.lottieAnimationView.setAnimation(R.raw.mist)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.clear_sky_bg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

}