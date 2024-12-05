package com.exal.testapp.view.detailexpense

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.DetailListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExpenseViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
    private val _productList = MutableLiveData<Resource<DetailListResponse>>()
    val productList: LiveData<Resource<DetailListResponse>> get() = _productList

    fun getExpenseDetail(id: Int) {
        viewModelScope.launch {
            dataRepository.getExpensesDetail(id)
                .collect { resource ->
                    _productList.value = resource
                }
        }
    }
}