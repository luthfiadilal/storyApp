package com.example.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class AddNewsResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
