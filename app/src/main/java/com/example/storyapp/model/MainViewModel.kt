package com.example.storyapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn

import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.response.ListStoryItem

class MainViewModel(userRepository: UserRepository): ViewModel() {


    val story : LiveData<PagingData<ListStoryItem>> =
        userRepository.getAllStories().cachedIn(viewModelScope)


}