package net.rishiz.weather

import android.app.Application

class WeatherApplication: Application() {
    companion object{
        lateinit var instance:WeatherApplication
    }
    override fun onCreate() {
        super.onCreate()
        instance=this@WeatherApplication
    }
}