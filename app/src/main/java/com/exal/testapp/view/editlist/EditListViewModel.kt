package com.exal.testapp.view.editlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.ResultListResponseItem
import com.exal.testapp.data.network.response.ScanImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditListViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {

    private val _resultList = MutableStateFlow<Resource<List<ResultListResponseItem>>>(Resource.Loading())
    val resultList: StateFlow<Resource<List<ResultListResponseItem>>> = _resultList

    private val _scanData = MutableLiveData<ScanImageResponse?>()
    val scanData: LiveData<ScanImageResponse?> get() = _scanData

    fun setScanData(scanImageResponse: ScanImageResponse?) {
        _scanData.value = scanImageResponse
    }

    fun fetchResultList(id: String) {
        viewModelScope.launch {
            if (_resultList.value !is Resource.Success) { // Hanya fetch data jika belum sukses
                _resultList.emit(Resource.Loading())
                repository.getResultList(id).collect { result ->
                    _resultList.emit(result)
                }
            }
        }
    }
}
