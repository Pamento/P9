package com.openclassrooms.realestatemanager.data.remote.client

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.openclassrooms.realestatemanager.data.remote.api.GoogleMapsAPI
import com.openclassrooms.realestatemanager.util.Constants.Constants
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient

object RetrofitClient {
//    private val retrofitBuilder = Retrofit.Builder()
//        .baseUrl(Constants.GOOGLE_BASE_URL)
//        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//        .addConverterFactory(GsonConverterFactory.create())
//    private val retrofit = retrofitBuilder.build()
    @JvmStatic
    val requestApi: GoogleMapsAPI
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.GOOGLE_BASE_URL)
                .client(OkHttpClient.Builder().addInterceptor(interceptor).build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GoogleMapsAPI::class.java)
        }
}