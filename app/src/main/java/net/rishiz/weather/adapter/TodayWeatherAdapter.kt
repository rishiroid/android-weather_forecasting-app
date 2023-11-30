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

class TodayWeatherAdapter :RecyclerView.Adapter<TodayWeatherHolder>(){

 private var listOfTodayWeather=listOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayWeatherHolder {
//        val inflater=LayoutInflater.from(parent.context)
       // val view=TodayWeatherRowBinding.inflate(inflater,parent,false)
        val view=LayoutInflater.from(parent.context).inflate(R.layout.today_weather_row,parent,false)
        return TodayWeatherHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfTodayWeather.size
    }

    override fun onBindViewHolder(holder: TodayWeatherHolder, position: Int) {
        val todayWeather=listOfTodayWeather[position]
        holder.time.text= todayWeather.dt_txt.substring(11,16)

        val tempFahrenheit=todayWeather.main.temp
        val tempCelsius=(tempFahrenheit.minus(273.15))
        val tempFormatted=String.format("%.2f",tempCelsius)
        holder.tempAtTimes.text=tempFormatted

        val calender=Calendar.getInstance()
        val dateFormat=SimpleDateFormat("HH:mm")
        val formatedTime=dateFormat.format(calender.time)
        val timeOfApi=todayWeather.dt_txt.split("")
        val partAfterSpace=timeOfApi[1]
        Log.d("Time","Formated Time:$formatedTime,TimeOfApi:$partAfterSpace")

        for( weather in todayWeather.weather){
//            if(i.description=="Haze"||i.description=="Rain"){
//                holder.timelottieAnimation.setAnimation(R.raw.haze)
//                holder.timelottieAnimation.
//            }
            when(weather.description){
                "Haze"->{
                    holder.timelottieAnimation.setAnimation(R.raw.haze)
                }
                else ->{
                    holder.timelottieAnimation.setAnimation(R.raw.sun)
                }
            }
            holder.timelottieAnimation.playAnimation()
        }


    }
    fun setList(newList:List<WeatherList>){
        this.listOfTodayWeather=newList
    }
}
class TodayWeatherHolder(binding: View) : RecyclerView.ViewHolder(binding){
    val time: TextView =binding.findViewById(R.id.time)
    val timelottieAnimation: LottieAnimationView =binding.findViewById(R.id.timelottieAnimation)
    val tempAtTimes: TextView =binding.findViewById(R.id.tempAtTime)

}