package com.wanotube.wanotubeapp.network

import com.google.gson.GsonBuilder
import com.wanotube.wanotubeapp.util.Constant.PORT
import com.wanotube.wanotubeapp.util.Constant.SYSTEM_URL
import com.wanotube.wanotubeapp.util.Constant.VERSION
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a wanotube playlist.
 */
interface WanoTubeService {
    @GET("videos")
    fun getVideos(): Call<NetworkVideoContainer>

    @GET("videos/")
    fun getVideo(@Query("id") id: String): Call<NetworkVideo>

}

/**
 * Main entry point for network access. Call like `WanoTubeNetwork.wanotubes.getVideos()`
 */
object WanoTubeNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("$SYSTEM_URL:$PORT$VERSION/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    val wanotubes: WanoTubeService = retrofit.create(WanoTubeService::class.java)
}
