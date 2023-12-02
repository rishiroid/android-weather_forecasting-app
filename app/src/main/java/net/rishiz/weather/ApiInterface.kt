package net.rishiz.weather


import net.rishiz.weather.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast?")
    fun getWeather(
        @Query("lat") lat:String,
        @Query("lon") lon:String,
        @Query("appid") appid:String=Utils.API_KEY
    ):Call<WeatherData>
    @GET("forecast?")
    fun getWeatherByCity(
        @Query("q") city:String,
        @Query("appid") appid:String=Utils.API_KEY
    ) : Call<WeatherData>

}