package com.exal.testapp.view.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.ExpenseListResponseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {
    fun getExpenses(id: String): LiveData<Resource<List<ExpenseListResponseItem>>> = liveData {
        emit(Resource.Loading())
        emitSource(repository.getExpensesList(id).asLiveData())
    }
}