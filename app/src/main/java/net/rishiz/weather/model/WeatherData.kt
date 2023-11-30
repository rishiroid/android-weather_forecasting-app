package net.rishiz.weather.model

data class WeatherData(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherList>,
    val message: Int
)