package com.exal.testapp.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanImageResponse(
	@field:SerializedName("products")
	val products: List<ProductsItem?>? = null
): Parcelable

@Parcelize
data class ProductsItem(
	val id: Int? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("detail")
	val detail: Detail? = null
): Parcelable

@Parcelize
data class Detail(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("category_probability")
	val categoryProbability: Double? = null
): Parcelable
