package com.example.storyapp.model

import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.storyapp.data.remote.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isLogin = MutableLiveData<String>()
    val login: LiveData<String> get() = _isLogin


    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        _isLoading.postValue(true)
        try {
            val response = userRepository.login(email, password)
            emit(response)
            _isLoading.postValue(false)

        } catch (e: HttpException) {
            _isLoading.postValue(false)

            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message.toString()
            _isLogin.postValue(errorMessage)
            Log.d("LoginViewModel", errorMessage)
        }
    }



}