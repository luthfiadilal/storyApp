package com.example.storyapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.User
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.response.GetAllStoriesResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MapViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uploadResponse = MutableLiveData<GetAllStoriesResponse>()
    val uploadResponse: LiveData<GetAllStoriesResponse> get() = _uploadResponse

    private val _getLocationResponse = MutableLiveData<String>()
    val getLocationResponse: LiveData<String> get() = _getLocationResponse

    fun getStoriesLocation(token: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getStoriesLocation(token)
                _uploadResponse.postValue(response)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message.toString()
                _getLocationResponse.postValue(errorMessage)
            }

        }
    }

    fun getSession(): LiveData<User> {
        return userRepository.getSession()
    }
}