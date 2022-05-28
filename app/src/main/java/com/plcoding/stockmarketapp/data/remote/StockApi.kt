package com.plcoding.stockmarketapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query


interface StockApi {
    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(@Query("apikey") apiKey:String):ResponseBody
    companion object{
        const val API_KEY = "N7C3AIW0F7BPMX74"
        const val BASE_URL = "https://alphavantage.co"
    }
}