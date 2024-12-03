package com.exal.testapp.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.GetListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {
    private val _expenses = MutableLiveData<Resource<GetListResponse>>()
    val expenses: LiveData<Resource<GetListResponse>> get() = _expenses

    fun getExpenseList() {
        viewModelScope.launch {
            dataRepository.getExpenseList()
                .collect { resource ->
                    _expenses.postValue(resource)
                }
        }
    }
}