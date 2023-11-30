package net.rishiz.weather.view.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import androidx.lifecycle.ViewModelProvider
import net.rishiz.weather.R
import net.rishiz.weather.RetrofitInstance
import net.rishiz.weather.SharedPrefs
import net.rishiz.weather.Utils
import net.rishiz.weather.adapter.TodayWeatherAdapter
import net.rishiz.weather.databinding.ActivityMainBinding
import net.rishiz.weather.model.WeatherData
import net.rishiz.weather.model.WeatherList
import net.rishiz.weather.viewmodel.WeatherViewModel
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG=MainActivity::class.java.canonicalName

    }
    private lateinit var  binding: ActivityMainBinding
    private lateinit var viewModel:WeatherViewModel
    private lateinit var adapter:TodayWeatherAdapter
    private val cityName="Chandrapur"

  //  private val shrdPref=SharedPrefs.getInstance(this@MainActivity)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        viewModel= ViewModelProvider(this)[WeatherViewModel::class.java]
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.lifecycleOwner=this
        binding.viewModel=viewModel
      // check location permission
      if (checkLocationPermission()) {
          getCurrentLocation()
      } else {
          requestLocationPermission()
      }
        viewModel.getWeather()


        adapter= TodayWeatherAdapter()

        // Whenever app runs cleare te city that searched previously
        val shrdPref=SharedPrefs.getInstance(this@MainActivity)
        shrdPref.clearCityValue()

        viewModel.todaysWeatherLiveDataList.observe(this){
            val wetherObserver= it as List<WeatherList>
            binding.recyclerview.adapter=adapter
            adapter.setList(wetherObserver)
            adapter.notifyDataSetChanged()

        }
        viewModel.closerToExactWeather.observe(this) {

            val tempFahrenheit = it!!.main.temp
            val temp_min = it.main.temp_min
            val temp_max = it.main.temp_max

            binding.temp.text = convertTempFaherenheitToCelsius(tempFahrenheit)
            binding.minTemp.text = convertTempFaherenheitToCelsius(temp_min)
            binding.maxTemp.text = convertTempFaherenheitToCelsius(temp_max)
            for (weather in it.weather) {
                binding.weather.text = weather.description
                changeImagesAccordingToWeather(weather.description)
            }

            val humidity = it.main.humidity
            val windspeed = it.wind.speed
            binding.humidity.text = "$humidity%"
            binding.windspeed.text = "$windspeed m/s"

//            val sunrise=it.city.sunrise
//            val sunset=it.city.sunset
//            val seaLevel=responseBody.main.pressure

            val inputFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(it.dt_txt)
            val outFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
            val dateNdayName = outFormat.format(date!!)
            Log.d(TAG, "dateNdayName:$dateNdayName")
            binding.date.text = dateNdayName
            binding.chanceofrain.text = "${it.pop}%"
        }


        searchCity()
        binding.nextdays.setOnClickListener{
            val intent= Intent(this,FutureForecastActivity::class.java)
            startActivity(intent)
        }

    }
    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission=ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission=ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),Utils.PERMISSION_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==Utils.PERMISSION_REQUEST_CODE) {
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
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            val location: Location?=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val latitude=location?.latitude
            val longitude=location?.longitude
            //set lon lat and get this in the viewmodel
            val shrdPref=SharedPrefs.getInstance(this@MainActivity)
            shrdPref.setValue("lat",latitude.toString())
            shrdPref.setValue("lon",longitude.toString())
//            viewModel.getWeather()
            Toast.makeText(this, "latitude:$latitude longitude:$longitude",Toast.LENGTH_SHORT).show()
            Log.d(TAG,"latitude:$latitude,longitude:$longitude")

            val lat=shrdPref.getValue("lat").toString()
            val lon=shrdPref.getValue("lon").toString()
            Log.d(TAG,"lat:$lat,lon:$lon")


        }
    }


    private fun convertTempFaherenheitToCelsius(tempFahrenheit: Double): String {
        val tempCelsius=(tempFahrenheit.minus(273.15))
        return String.format("%.2f",tempCelsius)
    }

    private fun searchCity() {
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextSubmit(query: String?): Boolean {
                val shrdPref=SharedPrefs.getInstance(this@MainActivity)
               shrdPref.setValuOrNull("city",query!!)
                if (query.isNotEmpty()) {
                    viewModel.getWeather(query)
                    binding.searchView.setQuery("",false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified=true

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        } )
    }

//    private fun fetchWeatherData(cityName:String) {
//        val response= RetrofitInstance.retrofit.getWeatherData(cityName,"3b35d702cbc2966bb04ef77f1e7c56aa","metric")
//        response.enqueue(object : retrofit2.Callback<WeatherData> {
//            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
//                val responseBody=response.body()
//                if(responseBody!=null && response.isSuccessful){
//                    responseBody.list.m
//                    val temp=responseBody.main.temp.toString()
//                    val humidity=responseBody.list.main.h
//                    val windspeed=responseBody.wind.speed
//                    val sunrise=responseBody.sys.sunrise
//                    val sunset=responseBody.sys.sunset
//                    val seaLevel=responseBody.main.pressure
////                    val minTemp=responseBody.main.temp_min
////                    val maxTemp=responseBody.main.temp_max
//                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
//
//                    binding.temp.text="$temp°C"
//                    binding.weather.text=condition
//                    binding.minTemp.text="Min: $minTemp°C"
//                    binding.maxTemp.text="Max: $maxTemp°C"
//                    binding.humidity.text="$humidity%"
//                    binding.windspeed.text="$windspeed m/s"
//                    binding.sunrise.text="${time(sunrise.toLong())}"
//                    binding.sunset.text="${time(sunset.toLong())}"
//                    binding.sea.text="$seaLevel hPa"
//                    binding.condition.text=condition
//                    binding.cityName.text=cityName
//                    binding.day.text=dayName(System.currentTimeMillis())
//                    binding.date.text=date()
//                    changeImagesAccordingToWeather(condition)
//
//                }
//            }
//
//            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

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