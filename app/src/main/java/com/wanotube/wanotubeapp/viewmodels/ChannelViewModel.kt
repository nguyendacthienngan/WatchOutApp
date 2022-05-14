package com.wanotube.wanotubeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.repository.ChannelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ChannelViewModel(application: Application) : AndroidViewModel(application) {
    private val channelRepository = ChannelRepository(getDatabase(application))

    var currentChannelVideos: MutableLiveData<MutableList<Video>> = MutableLiveData<MutableList<Video>>()
    
    init {
        refreshVideos()
    }

    fun clearDataFromRepository() {
        viewModelScope.launch {
            currentChannelVideos.value = mutableListOf()
            channelRepository.clearVideos()
        }
    }
    
    fun refreshVideos() {
        CoroutineScope(Dispatchers.IO).launch {
            channelRepository.getAllVideosByChannelId()?.enqueue(object :
                Callback<NetworkVideoContainer> {
                override fun onResponse(
                    call: Call<NetworkVideoContainer>?,
                    response: Response<NetworkVideoContainer?>?
                ) {
                    val databaseVideo = response?.body()?.asDatabaseModel() ?: listOf()
                    val videos = databaseVideo.asDomainModel()
                    currentChannelVideos.value = videos.toMutableList()
                }
                override fun onFailure(call: Call<NetworkVideoContainer>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }

    class ChannelViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChannelViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChannelViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}