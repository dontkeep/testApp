package com.exal.testapp.data.network.response

import com.google.gson.annotations.SerializedName

data class DetailListResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data2? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Data2(

	@field:SerializedName("detailItems")
	val detailItems: List<DetailItemsItem?>? = null,

	@field:SerializedName("receipt_image")
	val receiptImage: String? = null,

	@field:SerializedName("thumbnail_image")
	val thumbnailImage: String? = null
)

data class DetailItemsItem(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: String? = null
)
