package com.exal.testapp.view.expenses

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
    private val _expenses = MutableLiveData<PagingData<ListEntity>>()
    val expenses: LiveData<PagingData<ListEntity>> get() = _expenses

    fun getLists(type: String) {
        viewModelScope.launch {
            dataRepository.getListData(type)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _expenses.value = pagingData
                }
        }
    }
}