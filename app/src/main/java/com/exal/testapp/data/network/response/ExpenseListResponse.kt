package com.exal.testapp.data.network.response

import com.google.gson.annotations.SerializedName

data class ExpenseListResponse(

	@field:SerializedName("ExpenseListResponse")
	val expenseListResponse: List<ExpenseListResponseItem>
)

data class DataItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("list_total_price")
	val listTotalPrice: String,

	@field:SerializedName("list_id")
	val listId: String,

	@field:SerializedName("total_items")
	val totalItems: Int,

	@field:SerializedName("list_name")
	val listName: String
)

data class ExpenseListResponseItem(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("id_user")
	val idUser: String
)
