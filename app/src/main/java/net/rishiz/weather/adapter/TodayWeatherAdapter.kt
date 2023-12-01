package net.rishiz.weather.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import net.rishiz.weather.R
import net.rishiz.weather.model.WeatherList
import java.text.SimpleDateFormat
import java.util.Calendar

class TodayWeatherAdapter : RecyclerView.Adapter<TodayWeatherHolder>() {

    private var listOfTodayWeather = listOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayWeatherHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.today_weather_row, parent, false)
        return TodayWeatherHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfTodayWeather.size
    }

    override fun onBindViewHolder(holder: TodayWeatherHolder, position: Int) {
        val todayWeather = listOfTodayWeather[position]
        val time24 = todayWeather.dt_txt.substring(11, 16)
        holder.time.text = convertTo12HourFormat(time24)

        val tempFahrenheit = todayWeather.main.temp
        val tempCelsius = (tempFahrenheit.minus(273.15))
        val tempFormatted = String.format("%.2f", tempCelsius)
        "${tempFormatted}℃".also { holder.tempAtTimes.text = it }

        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:mm a")
        val formatedTime = dateFormat.format(calender.time)
        val timeOfApi = todayWeather.dt_txt.split("")
        val partAfterSpace = timeOfApi[1]
        Log.d("Time", "Formated Time:$formatedTime,TimeOfApi:$partAfterSpace")

        for (weather in todayWeather.weather) {
            when (weather.description) {

                "clear sky" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.sun)
                }

                "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.cloud)
                }

                "rain", "light rain", "shower rain", "moderate rain", "light intensity shower rain", "ragged shower rain" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.rain)
                }

                "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.heavy_rain)
                }

                "thunderstorm", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.thunderstorm)
                }

                "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.thunderstorm_with_rain)
                }

                "light snow", "snow", "heavy snow", "light shower sleet", "sleet", "ight shower sleet", "light rain and snow" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.snow_weather)
                }

                "mist", "smoke", "dust", "fog", "sand", "haze", "dust whirls", "volcanic ash", "squalls" -> {
                    holder.timelottieAnimation.setAnimation(R.raw.mist)
                }

                else -> {
                    holder.timelottieAnimation.setAnimation(R.raw.sun)
                }
            }
            holder.timelottieAnimation.playAnimation()
        }
    }


    fun setList(newList: List<WeatherList>) {
        this.listOfTodayWeather = newList
    }

    private fun convertTo12HourFormat(time24: String): String {
        val timeParts = time24.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1]

        val amPm = if (hour < 12) "AM" else "PM"
        val newHour = if (hour == 0 || hour == 12) 12 else hour % 12

        return "$newHour:$minute $amPm"
    }
}

class TodayWeatherHolder(binding: View) : RecyclerView.ViewHolder(binding) {
    val time: TextView = binding.findViewById(R.id.time)
    val timelottieAnimation: LottieAnimationView = binding.findViewById(R.id.timelottieAnimation)
    val tempAtTimes: TextView = binding.findViewById(R.id.tempAtTime)
}