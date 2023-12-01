package net.rishiz.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retrofit: ApiInterface = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Utils.BASE_URL)
        .build().create(ApiInterface::class.java)
}