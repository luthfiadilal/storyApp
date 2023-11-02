package com.example.storyapp.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.User
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.response.AddNewsResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddNewsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _uploadResponse = MutableLiveData<AddNewsResponse>()
    val uploadResponse: LiveData<AddNewsResponse> = _uploadResponse

    private val _isUpload = MutableLiveData<String>()
    val isUpload: LiveData<String> = _isUpload

    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.addStory(token, file, description)
                _uploadResponse.value = response
                _isLoading.value = false
                Log.d("AddViewModel", "addStory: ${Gson().toJson(response)}")
            } catch (e: HttpException) {
                _isLoading.value = false
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message.toString()
                _isUpload.postValue(errorMessage)
                Log.e("AddViewModel", "addStory: $errorMessage")
            }
        }
    }


    fun getSession():LiveData<User> {
        return userRepository.getSession()
    }
}