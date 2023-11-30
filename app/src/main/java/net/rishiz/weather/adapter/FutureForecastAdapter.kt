package net.rishiz.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import net.rishiz.weather.R
import net.rishiz.weather.Utils
import net.rishiz.weather.WeatherApplication
import net.rishiz.weather.databinding.FutureForecastRowBinding
import net.rishiz.weather.model.WeatherList
import java.text.SimpleDateFormat
import java.util.Locale


class FutureForecastAdapter :
    RecyclerView.Adapter<FutureForecastHolder>() {
    private var weatherList= listOf<WeatherList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureForecastHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=FutureForecastRowBinding.inflate(inflater,parent,false)
        return FutureForecastHolder(view)
    }

    override fun getItemCount(): Int {
       return weatherList.size
    }

    override fun onBindViewHolder(holder: FutureForecastHolder, position: Int) {

        val weatherObject=weatherList[position]

        for(i in weatherObject.weather){
            holder.desc.text= i.description
        }

        holder.humidity.text=weatherObject.main!!.humidity.toString()
        holder.windSpeed.text=weatherObject.wind.speed.toString()

        val tempFahrenheit=weatherObject.main?.temp
        val tempCelcius=(tempFahrenheit?.minus(273.15))
        val tempFormatted=String.format("%.2f",tempCelcius)
        holder.temp.text="${tempFormatted}℃"

        val inputFormat=SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date=inputFormat.parse(weatherObject.dt_txt)
        val outputFormat=SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
        val dateNday=outputFormat.format(date)
        holder.day.text=dateNday

        for( weather in weatherObject.weather){
//            if(i.description=="Haze"||i.description=="Rain"){
//                holder.timelottieAnimation.setAnimation(R.raw.haze)
//                holder.timelottieAnimation.
//            }

            if(weather.icon=="02d"){
                val url=Utils.ICON_URL+"02d.png"
               Picasso.get().load(url).into(holder.icon)
            }
            when(weather.description){
                "Haze"->{
                    holder.lottieAnimationView.setAnimation(R.raw.haze)
                }
                else ->{
                    holder.lottieAnimationView.setAnimation(R.raw.sun)
                    val url=Utils.ICON_URL+"02d@2x.png"
                    Picasso.get().load(url).into(holder.icon)
//                    Glide.with(WeatherApplication.instance).load(url).into(holder.icon)

                }
            }
            holder.lottieAnimationView.playAnimation()
        }

    }
    fun setList(newList:List<WeatherList>){
        this.weatherList=newList
    }
}
class FutureForecastHolder(binding: FutureForecastRowBinding) :RecyclerView.ViewHolder(binding.root){
    val day=binding.nextDay
    val lottieAnimationView=binding.nextDayAnimation
    val temp=binding.nextDayTemp
    val icon=binding.stausIcon
    val desc=binding.weatherDescr
    val windSpeed=binding.nextDayWind
    val humidity=binding.nextDayHumadity

}