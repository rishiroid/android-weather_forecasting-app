package net.rishiz.weather.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import net.rishiz.weather.R
import net.rishiz.weather.RetrofitInstance
import net.rishiz.weather.databinding.ActivityMainBinding
import net.rishiz.weather.model.WeatherData
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val cityName="Chandrapur"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData(cityName)
        searchCity()
        binding.nextdays.setOnClickListener{
            val intent= Intent(this,FutureForecastActivity::class.java)
            startActivity(intent)
        }

    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        } )
    }

    private fun fetchWeatherData(cityName:String) {
        val response= RetrofitInstance.retrofit.getWeatherData(cityName,"3b35d702cbc2966bb04ef77f1e7c56aa","metric")
        response.enqueue(object : retrofit2.Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val responseBody=response.body()
                if(responseBody!=null && response.isSuccessful){
                    val temp=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunrise=responseBody.sys.sunrise
                    val sunset=responseBody.sys.sunset
                    val seaLevel=responseBody.main.pressure
                    val minTemp=responseBody.main.temp_min
                    val maxTemp=responseBody.main.temp_max
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"

                    binding.temp.text="$temp°C"
                    binding.weather.text=condition
                    binding.minTemp.text="Min: $minTemp°C"
                    binding.maxTemp.text="Max: $maxTemp°C"
                    binding.humidity.text="$humidity%"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise.toLong())}"
                    binding.sunset.text="${time(sunset.toLong())}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text=condition
                    binding.cityName.text=cityName
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    changeImagesAccordingToWeather(condition)

                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
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
    fun time(timestamp:Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}