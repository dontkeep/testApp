package com.exal.testapp.data.network.response

import com.google.gson.annotations.SerializedName

data class ProfileDetailResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class UserProfile(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)

data class Data(

	@field:SerializedName("userProfile")
	val userProfile: UserProfile
)
