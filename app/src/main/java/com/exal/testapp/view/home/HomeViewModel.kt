package com.exal.testapp.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.local.entity.ListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {
    private val _expenses = MutableLiveData<PagingData<ListEntity>>()
    val expenses: LiveData<PagingData<ListEntity>> get() = _expenses

    private val _totalExpenses = MutableLiveData<Int>()
    val totalExpenses: LiveData<Int> get() = _totalExpenses

    private val _totalItems = MutableLiveData<Int>()
    val totalItems: LiveData<Int> get() = _totalItems

    fun getLists(type: String) {
        viewModelScope.launch {
            dataRepository.getFiveListData(type)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _expenses.postValue(pagingData)
                }
        }
    }
}