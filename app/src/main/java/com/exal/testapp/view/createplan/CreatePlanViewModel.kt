package com.exal.testapp.view.createplan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.PostListResponse
import com.exal.testapp.data.network.response.ProductsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreatePlanViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {
    private val _productList = MutableLiveData<List<ProductsItem>>()
    val productList: LiveData<List<ProductsItem>> get() = _productList

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    init{
        _productList.value = emptyList()
        _totalPrice.value = 0
    }

    fun postData(
        title: RequestBody,
        receiptImage: MultipartBody.Part?,
        thumbnailImage: MultipartBody.Part?,
        productItems: RequestBody,
        type: RequestBody,
        totalExpenses: RequestBody,
        totalItems: RequestBody
    ): Flow<Resource<PostListResponse>> = dataRepository.postData(
        title,
        receiptImage,
        thumbnailImage,
        productItems,
        type,
        totalExpenses,
        totalItems
    )

    fun deleteProduct(item: ProductsItem) {
        val currentList = _productList.value.orEmpty().toMutableList()
        currentList.remove(item)
        _productList.value = currentList

        val newTotalPrice = currentList.sumOf { (it.price ?: 0) * (it.amount ?: 0) }
        _totalPrice.value = newTotalPrice
    }

    fun addProduct(product: ProductsItem) {
        val currentList = _productList.value.orEmpty().toMutableList()
        currentList.add(product)
        _productList.value = currentList
        Log.d("CreatePlanViewModel", "Added product: $product")

        val newTotalPrice = currentList.sumOf { (it.price ?: 0) * (it.amount ?: 0) }
        _totalPrice.value = newTotalPrice
        Log.d("CreatePlanViewModel", "Updated total price: $newTotalPrice")
    }
}