package net.rishiz.weather.model

import com.airbnb.lottie.LottieAnimationView

data class HourlyWeather(
    val hour:String,
    val lottieAnimationView:LottieAnimationView,
    val temp:String)
