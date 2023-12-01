package net.rishiz.weather.view.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import net.rishiz.weather.R
import net.rishiz.weather.SharedPrefs
import net.rishiz.weather.Utils
import net.rishiz.weather.adapter.TodayWeatherAdapter
import net.rishiz.weather.databinding.ActivityMainBinding
import net.rishiz.weather.model.WeatherList
import net.rishiz.weather.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.canonicalName
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: TodayWeatherAdapter

    private var shrdPref: SharedPrefs? =null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shrdPref=SharedPrefs.getInstance(this)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        // check location permission
        if (checkLocationPermission()) {
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
        viewModel.getWeather()

        adapter = TodayWeatherAdapter()

        // Whenever app runs cleare te city that searched previously
        //  val shrdPref = SharedPrefs.getInstance(this@MainActivity)

        shrdPref?.clearCityValue()

        viewModel.todaysWeatherLiveDataList.observe(this) {
            val wetherObserver = it as List<WeatherList>
            binding.recyclerview.adapter = adapter
            adapter.setList(wetherObserver)
        }

        viewModel.closerToExactWeather.observe(this) { it ->

            val tempFahrenheit = it!!.main.temp
            val temp_min = it.main.temp_min
            val temp_max = it.main.temp_max

            binding.temp.text = "${convertTempFaherenheitToCelsius(tempFahrenheit)}℃"
            "${convertTempFaherenheitToCelsius(temp_min)}℃".also { binding.minTemp.text = it }
            "${convertTempFaherenheitToCelsius(temp_max)}℃".also { binding.maxTemp.text = it }
            for (weather in it.weather) {
                binding.weather.text = weather.description
                changeImagesAccordingToWeather(weather.description)
            }

            val humidity = it.main.humidity
            val windspeed = it.wind.speed
            val sea_level = it.main.sea_level

            "$humidity%".also { binding.humidity.text = it }
            "$windspeed m/s".also { binding.windspeed.text = it }
            "$sea_level hPa".also { binding.sea.text = it }

            val inputFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(it.dt_txt)
            val outFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
            val dateNdayName = outFormat.format(date!!)

            Log.d(TAG, "dateNdayName:$dateNdayName")
            binding.date.text = dateNdayName
            binding.chanceofrain.text = "${it.pop}%"
        }

        searchCity()
        binding.nextdays.setOnClickListener {
            val intent = Intent(this, FutureForecastActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), Utils.PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else { //permisioon denied handle}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location: Location? =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val latitude = location?.latitude
            val longitude = location?.longitude

            //set lon lat and get this in the viewmodel
            shrdPref?.setValue("lat", latitude.toString())
            shrdPref?.setValue("lon", longitude.toString())

            Toast.makeText(this, "latitude:$latitude longitude:$longitude", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "latitude:$latitude,longitude:$longitude")

            val lat = shrdPref?.getValue("lat").toString()
            val lon = shrdPref?.getValue("lon").toString()
            Log.d(TAG, "lat:$lat,lon:$lon")


        }
    }
    private fun convertTempFaherenheitToCelsius(tempFahrenheit: Double): String {
        val tempCelsius = (tempFahrenheit.minus(273.15))
        return String.format("%.2f", tempCelsius)
    }
    private fun searchCity() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextSubmit(query: String?): Boolean {
                val shrdPref = SharedPrefs.getInstance(this@MainActivity)
                shrdPref.setValuOrNull("city", query!!)
                if (query.isNotEmpty()) {
                    viewModel.getWeather(query)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified = true

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
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

            "rain", "light rain", "shower rain", "moderate rain", "light intensity shower rain",
            "ragged shower rain" -> {
                //rain
                binding.root.setBackgroundResource(R.drawable.rain_bg)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain" -> {
                //heavy rain
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

            "light snow", "snow", "heavy snow", "light shower sleet", "sleet", "ight shower sleet", "light rain and snow"
            -> {
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