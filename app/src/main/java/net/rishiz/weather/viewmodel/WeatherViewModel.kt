package net.rishiz.weather.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.rishiz.weather.RetrofitInstance
import net.rishiz.weather.SharedPrefs
import net.rishiz.weather.WeatherApplication
import net.rishiz.weather.model.WeatherList
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class WeatherViewModel : ViewModel() {
    companion object {
        private val TAG = WeatherViewModel::class.java.canonicalName
    }

    val todaysWeatherLiveDataList = MutableLiveData<List<WeatherList>>()
    val tomorrowWeatherData = MutableLiveData<List<WeatherList>?>()
    val futurWeatherLiveDataList = MutableLiveData<List<WeatherList>>()
    val closerToExactWeather = MutableLiveData<WeatherList?>()
    val cityName = MutableLiveData<String>()
    val sunrise = MutableLiveData<String?>()
    val sunset = MutableLiveData<String?>()
    val context = WeatherApplication.instance
    private val sharedPrefs = SharedPrefs.getInstance(context)


    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeather(city: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val todaysWeatherList = mutableListOf<WeatherList>()

        val currentDateTime = LocalDateTime.now()
        val currentDateFormated = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val lat = sharedPrefs.getValue("lat").toString()
        val lon = sharedPrefs.getValue("lon").toString()
        Log.d(TAG, "lat:$lat,lon:$lon")
        val call = if (city != null) {
            RetrofitInstance.retrofit.getWeatherByCity(city)
        } else {
            RetrofitInstance.retrofit.getWeather(lat, lon)
        }
        val response = call.execute()
        if (response.isSuccessful) {
            val weatherList = response.body()?.list
            val city = response.body()?.city!!.name
            val sunRise = response.body()?.city?.sunrise?.let { time(it.toLong()) }
            val sunSet = response.body()?.city?.sunset?.let { time(it.toLong()) }

            cityName.postValue(city)
            sunrise.postValue(sunRise)
            sunset.postValue(sunSet)
            weatherList?.forEach {
                //separate all the weather objects that have the date of today
                Log.d(TAG,
                    "date from api:" + it.dt_txt.split("\\s".toRegex())
                        .contains(currentDateFormated)
                )
                if (it.dt_txt.split("\\s".toRegex()).contains(currentDateFormated)) {
                    todaysWeatherList.add(it)
                }
            }
            //if api time closet to system time display that
            //if api time matches the sytem time then also display that
            val closetWeather = findClosetWeather(todaysWeatherList)
            closerToExactWeather.postValue(closetWeather)
            todaysWeatherLiveDataList.postValue(todaysWeatherList)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFutureWeather(city: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val futureWeatherList = mutableListOf<WeatherList>()
        val currentDateTime = LocalDateTime.now()
        val currentDateFormated = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val tommorrowDateTime = LocalDateTime.now().plusDays(1)
        val tommorrowDateTimeformated =
            tommorrowDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val lat = sharedPrefs.getValue("lat").toString()
        val lon = sharedPrefs.getValue("lon").toString()
        Log.d(TAG, "lat:$lat,lon:$lon")

        val call = if (city != null) {
            RetrofitInstance.retrofit.getWeatherByCity(city)
        } else {
            RetrofitInstance.retrofit.getWeather(lat, lon)
        }
        val response = call.execute()
        if (response.isSuccessful) {
            val weatherList = response.body()?.list

            val tomorrowWeatherList = weatherList?.filter { weather ->
                weather.dt_txt.split("\\s".toRegex()).contains(tommorrowDateTimeformated)
            }
            tomorrowWeatherData.postValue(tomorrowWeatherList)
            weatherList?.forEach {
                //separate all the weather objects that have the date of today and tommorrow
                if (!it.dt_txt.split("\\s".toRegex())
                        .contains(currentDateFormated) && !it.dt_txt.split("\\s".toRegex())
                        .contains(tommorrowDateTimeformated)
                ) {
                    Log.d(TAG, "split" + it.dt_txt.split("\\s".toRegex()))
                    Log.d(TAG, "substring" + it.dt_txt.substring(16, 19))
                    if (it.dt_txt.substring(11, 16) == "12:00") {
                        futureWeatherList.add(it)
                    }
                }
            }
            futurWeatherLiveDataList.postValue(futureWeatherList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findClosetWeather(weatherList: List<WeatherList>): WeatherList? {
        val systemTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        var closetWeather: WeatherList? = null
        var minTimeDifference = Int.MAX_VALUE
        for (weather in weatherList) {
            val weatherTime = weather.dt_txt.substring(11, 16)
            val timeDiff = abs(timeToMinutes(weatherTime)) - abs(timeToMinutes(systemTime))
            if (timeDiff < minTimeDifference) {
                minTimeDifference = timeDiff
                closetWeather = weather
            }
        }
        return closetWeather
    }

    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }

    fun convertTempFaherenheitToCelsius(tempFahrenheit: Double): String {
        val tempCelsius = (tempFahrenheit.minus(273.15))
        return String.format("%.2f", tempCelsius)
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

}
