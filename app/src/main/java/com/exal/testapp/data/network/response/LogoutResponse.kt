package com.exal.testapp.data.network.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: Message? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Message(

	@field:SerializedName("message")
	val message: String? = null
)
