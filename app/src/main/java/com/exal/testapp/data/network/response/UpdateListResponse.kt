package com.exal.testapp.data.network.response

import com.google.gson.annotations.SerializedName

data class UpdateListResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
