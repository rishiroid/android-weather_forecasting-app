package net.rishiz.weather.model

import com.airbnb.lottie.LottieAnimationView

data class FututreForecast(
    val day:String,
    val animationView: LottieAnimationView,
    val status:String,
    val maxTemp:Int,
    val minTemp:Int
)
