package net.rishiz.weather.view.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import net.rishiz.weather.R
import net.rishiz.weather.SharedPrefs
import net.rishiz.weather.adapter.FutureForecastAdapter
import net.rishiz.weather.databinding.ActivityFutureForecastBinding
import net.rishiz.weather.model.WeatherList
import net.rishiz.weather.viewmodel.WeatherViewModel

class FutureForecastActivity : AppCompatActivity() {
companion object{
    private val TAG=FutureForecastActivity::class.java.canonicalName
}

    private lateinit var viewModel: WeatherViewModel
    private lateinit var futureForecastAdapter: FutureForecastAdapter
    private lateinit var recyclerView:RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_forecast)
        viewModel= ViewModelProvider(this)[WeatherViewModel::class.java]
        futureForecastAdapter= FutureForecastAdapter()
        recyclerView=findViewById(R.id.recyclerView)
        val shardPref=SharedPrefs.getInstance(this)
        val city=shardPref.getValueOrNull("city")

        if(city!=null){
            viewModel.getFutureWeather(city)
        }else{
            Log.d("future","on")
            viewModel.getFutureWeather()
        }

        viewModel.futurWeatherLiveDataList.observe(this) {
            val setWeatherList = it as List<WeatherList>
            Log.d("FutureForecastActivity", setWeatherList.toString())
            recyclerView.adapter = futureForecastAdapter
            futureForecastAdapter.setList(setWeatherList)

        }
    }
}