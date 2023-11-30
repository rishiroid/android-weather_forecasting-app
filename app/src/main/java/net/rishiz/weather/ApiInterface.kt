package net.rishiz.weather


import net.rishiz.weather.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/forecast?q=chandrapur&appid=3b35d702cbc2966bb04ef77f1e7c56aa
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
//        @Query("units") units:String
    ) : Call<WeatherData>

}