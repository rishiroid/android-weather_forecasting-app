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

                    val temp=viewModel.convertTempFaherenheitToCelsius(weather.main.temp)
                    val temp_min=viewModel.convertTempFaherenheitToCelsius(weather.main.temp_min)
                    val temp_max=viewModel.convertTempFaherenheitToCelsius(weather.main.temp_max)
                    binding.temp.text = "${temp}\u2103"
                    binding.minTemp.text = "${temp_min}\u2103"
                    binding.maxTemp.text = "${temp_max}\u2103"
                    binding.humidity.text="${weather.main.humidity}%"
                    binding.windspeed.text="${weather.wind.speed}%"
                    binding.chanceofrain.text="${weather.pop}%"
                    for(i in weather.weather){
                        val description=i.description
                        binding.weatherDscr.text=description
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
        when(condition){
            "Haze"->{
                binding.root.setBackgroundResource(R.drawable.cloud_bg)
                binding.lottieAnimationView.setAnimation(R.raw.haze)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_bg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }
}