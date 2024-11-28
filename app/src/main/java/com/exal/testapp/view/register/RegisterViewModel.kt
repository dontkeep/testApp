package com.exal.testapp.view.register

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
class RegisterViewModel @Inject constructor(private val dataRepository: DataRepository): ViewModel() {
    private val _registerState = MutableLiveData<Resource<Boolean>>()
    val registerState: LiveData<Resource<Boolean>> get() = _registerState

    fun register(name: String, email: String, password: String, passwordRepeat: String) {
        viewModelScope.launch {
            dataRepository.register(name, email, password, passwordRepeat).collect { resource ->
                _registerState.postValue(resource)
            }
        }
    }
}