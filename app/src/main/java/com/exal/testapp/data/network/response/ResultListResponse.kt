package com.exal.testapp.data.network.response

import com.google.gson.annotations.SerializedName

data class ResultListResponse(

	@field:SerializedName("ResultListResponse")
	val resultListResponse: List<ResultListResponseItem>
)

data class ResultListResponseItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("list_id")
	val listId: String,

	@field:SerializedName("total_price")
	val totalPrice: String,

	@field:SerializedName("list_name")
	val listName: String,

	@field:SerializedName("total_items")
	val totalItems: Int,

	@field:SerializedName("items")
	val items: List<ItemsItem>
)

data class ItemsItem(

	@field:SerializedName("list_id")
	val listId: String,

	@field:SerializedName("item_id")
	val itemId: String,

	@field:SerializedName("item_price")
	val itemPrice: Int,

	@field:SerializedName("item_name")
	val itemName: String,

	@field:SerializedName("item_quantity")
	val itemQuantity: Int,

	@field:SerializedName("item_category")
	val itemCategory: String
)
