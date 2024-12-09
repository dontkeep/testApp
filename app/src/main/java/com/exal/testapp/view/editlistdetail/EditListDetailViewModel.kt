package com.exal.testapp.view.editlistdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.PostListResponse
import com.exal.testapp.data.network.response.DetailItemsItem
import com.exal.testapp.data.network.response.UpdateListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditListDetailViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
    private val _productList = MutableLiveData<List<DetailItemsItem>>()
    val productList: LiveData<List<DetailItemsItem>> get() = _productList

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    fun setInitialProductList(products: List<DetailItemsItem>) {
        _productList.value = products
        val newTotalPrice = products.sumOf { (it.price?.toDouble()?.toInt() ?: 0) * (it.amount ?: 0) }
        _totalPrice.value = newTotalPrice
    }

    fun updateData(
        id: Int,
        title: RequestBody,
        productItems: RequestBody,
        type: RequestBody,
        totalExpenses: RequestBody,
        totalItems: RequestBody,
    ): Flow<Resource<UpdateListResponse>> = dataRepository.updateList(
        id,
        title,
        productItems,
        type,
        totalExpenses,
        totalItems
    )

    fun updateProduct(product: DetailItemsItem) {
        _productList.value = _productList.value?.map {
            if (it.id == product.id) product else it
        }
        val newTotalPrice = _productList.value?.sumOf { (it.price?.toInt() ?: 0) * (it.amount ?: 0) } ?: 0
        _totalPrice.value = newTotalPrice
    }

}