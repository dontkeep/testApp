package com.exal.testapp.view.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.withTransaction
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.local.AppDatabase
import com.exal.testapp.data.local.entity.ListEntity
import com.exal.testapp.data.network.response.GetListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(private val dataRepository: DataRepository, private val database: AppDatabase) : ViewModel() {
    private val _planList = MutableLiveData<PagingData<ListEntity>>()
    val planList: LiveData<PagingData<ListEntity>> get() = _planList

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> get() = _toastEvent

    fun getLists(type: String) {
        viewModelScope.launch {
            dataRepository.getListData(type)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _planList.value = pagingData
                }
        }
    }

    fun filterData(type: String, month: Int, year: Int) {
        viewModelScope.launch {
            dataRepository.getFilterData(type, month, year, _toastEvent)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _planList.value = pagingData
                }
        }
    }
}