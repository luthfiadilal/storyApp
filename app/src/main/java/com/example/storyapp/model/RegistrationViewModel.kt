package com.example.storyapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.response.RegisterRespone
import com.google.gson.Gson

import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.HttpException

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registration = MutableLiveData<RegisterRespone>()
    val registration: LiveData<RegisterRespone> = _registration

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRegist = MutableLiveData<String>()
    val isRegist: LiveData<String> get() = _isRegist

    fun register(name: String, email: String, password: String)  {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = userRepository.register(name, email, password)
                _registration.postValue(response)
                _isLoading.postValue(false)
            } catch (e: HttpException) {
                _isLoading.postValue(false)
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message.toString()
                _isRegist.postValue(errorMessage)

            }
        }

    }
}