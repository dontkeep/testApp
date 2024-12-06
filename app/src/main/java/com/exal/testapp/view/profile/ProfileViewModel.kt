package com.exal.testapp.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {
    private val _logoutState = MutableLiveData<Resource<Boolean>>()
    val logoutState: LiveData<Resource<Boolean>> get() = _logoutState

    fun logout() {
        viewModelScope.launch {
            dataRepository.logout().collect { resource ->
                _logoutState.postValue(resource)
            }
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            dataRepository.deleteAllDatabaseData().collect { resource ->
                _logoutState.postValue(resource)
            }
        }
    }
}