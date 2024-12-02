package com.exal.testapp.view.editlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.data.network.response.ScanImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditListViewModel @Inject constructor() : ViewModel() {

    private val _scanData = MutableLiveData<ScanImageResponse?>()
    val scanData: LiveData<ScanImageResponse?> get() = _scanData

    fun setScanData(scanImageResponse: ScanImageResponse?) {
        if (_scanData.value == null) {
            scanImageResponse?.let { response ->
                val productsWithId =
                    response.products?.filterNotNull()?.mapIndexed { index, product ->
                        product.copy(id = index + 1)
                    }
                _scanData.value = response.copy(products = productsWithId)
            }
        }
    }

    fun updateItem(updatedItem: ProductsItem) {
        _scanData.value?.let { currentData ->
            val updatedProducts = currentData.products?.map { product ->
                if (product?.id == updatedItem.id) {
                    updatedItem
                } else {
                    product
                }
            }
            _scanData.value = currentData.copy(products = updatedProducts)
        }
    }
}
