package com.exal.testapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.ApiServices
import com.exal.testapp.helper.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiService: ApiServices // Retrofit or your API service
): ViewModel() {

    private val _loginState = MutableLiveData<Resource<Boolean>>()
    val loginState: LiveData<Resource<Boolean>> get() = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.postValue(Resource.Loading())
            try {
                val response = apiService.login(username, password)
                if (response.status == true && response.data != null) {
                    val token = response.data
                    token.let {
                        tokenManager.saveToken(it)
                        _loginState.postValue(Resource.Success(true))
                    }
                } else {
                    _loginState.postValue(Resource.Error("Login failed: ${response.message}"))
                }
            } catch (e: Exception) {
                _loginState.postValue(Resource.Error("An error occurred: ${e.localizedMessage}"))
            }
        }
    }

}