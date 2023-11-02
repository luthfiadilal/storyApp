package com.example.storyapp.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.network.ApiService
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.pref.User
import com.example.storyapp.data.response.AddNewsResponse
import com.example.storyapp.data.response.GetAllStoriesResponse
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.RegisterRespone
import com.example.storyapp.data.source.StoryPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody


class UserRepository(private val preferenceManager: PreferenceManager, private val apiService: ApiService) {

    fun getAllStories() : LiveData<PagingData<ListStoryItem>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, preferenceManager)
            }
        )
        val livedata = pager.liveData

        return livedata
    }


    suspend fun register(name: String, email: String, password: String) : RegisterRespone {
        return apiService.register(name, email, password)
    }


    suspend fun login(email: String, password: String) =
        apiService.login(email, password)



    suspend fun addStory(token: String, file: MultipartBody.Part, description: RequestBody) : AddNewsResponse {
        return apiService.addStory(token, file, description)
    }


    suspend fun getStoriesLocation(token: String) : GetAllStoriesResponse {
        return apiService.getStoriesLocation(token)
    }


    fun getSession() :  LiveData<User> {
        return preferenceManager.getSession().asLiveData()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: PreferenceManager,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

}


