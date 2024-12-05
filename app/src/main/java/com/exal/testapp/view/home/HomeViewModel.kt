package com.exal.testapp.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.local.entity.ListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {
    private val _expenses = MutableLiveData<PagingData<ListEntity>>()
    val expenses: LiveData<PagingData<ListEntity>> get() = _expenses

//    fun getLists(type: String): Flow<PagingData<ListEntity>> {
//        val flow = dataRepository.getFiveLatestData(type).cachedIn(viewModelScope)
//        flow.onEach { pagingData ->
//            _expenses.value = pagingData
//            Log.d("ExpensesViewModel", "PagingData Collected: $pagingData")
//        }
//        return flow
//    }
}