package net.rishiz.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import net.rishiz.weather.R
import net.rishiz.weather.databinding.HourlyWeatherRowBinding
import net.rishiz.weather.model.HourlyWeather

class HourlyWeatherAdapter (private val data:List<HourlyWeather>):RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=HourlyWeatherRowBinding.inflate(inflater,parent,false)
        return HourlyWeatherHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HourlyWeatherHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class HourlyWeatherHolder(binding: HourlyWeatherRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

}