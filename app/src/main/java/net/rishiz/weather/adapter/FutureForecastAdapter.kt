package net.rishiz.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.rishiz.weather.R
import net.rishiz.weather.Utils
import net.rishiz.weather.databinding.FutureForecastRowBinding
import net.rishiz.weather.model.WeatherList
import java.text.SimpleDateFormat
import java.util.Locale

class FutureForecastAdapter : RecyclerView.Adapter<FutureForecastHolder>() {
    private var weatherList = listOf<WeatherList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureForecastHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = FutureForecastRowBinding.inflate(inflater, parent, false)
        return FutureForecastHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: FutureForecastHolder, position: Int) {

        val weatherObject = weatherList[position]

        for (i in weatherObject.weather) {
            holder.desc.text = i.description
        }

        holder.humidity.text = "${weatherObject.main.humidity}%"

        "${weatherObject.wind.speed} m/s".also { holder.windSpeed.text = it }

        val tempFahrenheit = weatherObject.main.temp
        val tempCelcius = (tempFahrenheit.minus(273.15))
        val tempFormatted = String.format("%.2f", tempCelcius)
        "${tempFormatted}℃".also { holder.temp.text = it }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(weatherObject.dt_txt)
        val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
        val dateNday = date?.let { outputFormat.format(it) }
        holder.day.text = dateNday

        for (weather in weatherObject.weather) {

            if (weather.icon == "02d") {
                val url = Utils.ICON_URL + "02d.png"
                Picasso.get().load(url).into(holder.icon)
            }
            when (weather.icon) {
                "01d" -> {
                    val url = Utils.ICON_URL + "01d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "01n" -> {
                    val url = Utils.ICON_URL + "01n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "02d" -> {
                    val url = Utils.ICON_URL + "02d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "02n" -> {
                    val url = Utils.ICON_URL + "02n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "03d" -> {
                    val url = Utils.ICON_URL + "03d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "04d" -> {
                    val url = Utils.ICON_URL + "04d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "04n" -> {
                    val url = Utils.ICON_URL + "04n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "09d" -> {
                    val url = Utils.ICON_URL + "09d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "09n" -> {
                    val url = Utils.ICON_URL + "09n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "10d" -> {
                    val url = Utils.ICON_URL + "10d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "10n" -> {
                    val url = Utils.ICON_URL + "10n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "11d" -> {
                    val url = Utils.ICON_URL + "11d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "11n" -> {
                    val url = Utils.ICON_URL + "11n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "13d" -> {
                    val url = Utils.ICON_URL + "13d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "13n" -> {
                    val url = Utils.ICON_URL + "13n@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "50d" -> {
                    val url = Utils.ICON_URL + "50d@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

                "50n" -> {
                    val url = Utils.ICON_URL + "50@2x.png"
                    //Glide.with(WeatherApplication.instance).load(url).into(holder.icon)
                    Picasso.get().load(url).into(holder.icon)
                }

            }
            when (weather.description) {
                "clear sky" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.sun)
                }

                "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.cloud)
                }

                "rain", "light rain", "shower rain", "moderate rain", "light intensity shower rain", "ragged shower rain" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.rain)
                }

                "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.heavy_rain)
                }

                "thunderstorm", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.thunderstorm)
                }

                "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.thunderstorm_with_rain)
                }

                "light snow", "snow", "heavy snow", "light shower sleet", "sleet", "ight shower sleet", "light rain and snow" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.snow_weather)
                }

                "mist", "smoke", "dust", "fog", "sand", "haze", "dust whirls", "volcanic ash", "squalls" -> {
                    holder.lottieAnimationView.setAnimation(R.raw.mist)
                }

                else -> {
                    holder.lottieAnimationView.setAnimation(R.raw.sun)
                }
            }
            holder.lottieAnimationView.playAnimation()
        }
    }

    fun setList(newList: List<WeatherList>) {
        this.weatherList = newList
    }
}

class FutureForecastHolder(binding: FutureForecastRowBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val day = binding.nextDay
    val lottieAnimationView = binding.nextDayAnimation
    val temp = binding.nextDayTemp
    val icon = binding.stausIcon
    val desc = binding.weatherDescr
    val windSpeed = binding.nextDayWind
    val humidity = binding.nextDayHumadity
}