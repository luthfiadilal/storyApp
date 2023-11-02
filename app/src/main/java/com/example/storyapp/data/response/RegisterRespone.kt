package com.example.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class RegisterRespone (

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
