package com.exal.testapp.view.editlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.data.network.response.ScanImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditListViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _scanData = MutableLiveData<ScanImageResponse?>()
    val scanData: LiveData<ScanImageResponse?> get() = _scanData

    fun setScanData(scanImageResponse: ScanImageResponse?) {
        if (_scanData.value == null) { // Hanya set sekali
            scanImageResponse?.let { response ->
                val productsWithId = response.products?.filterNotNull()?.mapIndexed { index, product ->
                    product.copyWithId(index)
                }
                _scanData.value = response.copy(products = productsWithId)
            }
        }
    }

    // Extension function to add ID to ProductsItem
    private fun ProductsItem.copyWithId(id: Int): ProductsItem {
        return this.copy(detail = this.detail?.copy(type = id.toString())) // Contoh, gunakan field `type` untuk menyimpan ID.
    }


    fun updateItem(updatedItem: ProductsItem) {
        _scanData.value?.let { currentData ->
            val updatedProducts = currentData.products?.map { product ->
                if (product?.detail?.type == updatedItem.detail?.type) { // Bandingkan berdasarkan ID
                    updatedItem
                } else {
                    product
                }
            }
            _scanData.value = currentData.copy(products = updatedProducts)
        }
    }


}
