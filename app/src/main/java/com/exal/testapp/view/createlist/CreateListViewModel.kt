package com.exal.testapp.view.createlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exal.testapp.data.network.response.ProductsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateListViewModel @Inject constructor() : ViewModel() {
    private val _productList = MutableLiveData<List<ProductsItem>>()
    val productList: LiveData<List<ProductsItem>> get() = _productList

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    fun setProductList(products: List<ProductsItem>, price: Int) {
        _productList.value = products
        _totalPrice.value = price
    }

    fun deleteProduct(item: ProductsItem) {
        val currentList = _productList.value.orEmpty().toMutableList()
        currentList.remove(item)
        _productList.value = currentList

        val newTotalPrice = currentList.sumOf { (it.price ?: 0) * (it.amount ?: 0) }
        _totalPrice.value = newTotalPrice
    }
}