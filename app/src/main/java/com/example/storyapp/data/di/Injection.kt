package com.example.storyapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.network.ApiConfig
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.UserRepository


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val pref = PreferenceManager.getInstance(context.dataStore)
        return UserRepository.getInstance(pref, apiService)
    }
}