package com.exal.testapp.data

import android.util.Log
import com.exal.testapp.data.network.ApiServices
import com.exal.testapp.data.network.response.ExpenseListResponseItem
import com.exal.testapp.data.network.response.ResultListResponseItem
import com.exal.testapp.helper.manager.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val apiService: ApiServices, private val tokenManager: TokenManager) {
     fun getExpensesList(id: String): Flow<Resource<List<ExpenseListResponseItem>>> = flow {
            emit(Resource.Loading()) // Emit loading state
            try {
                val data = apiService.getExpensesList(id) // Fetch data from API
                emit(Resource.Success(data)) // Emit success state with data
            } catch (exception: Exception) {
                emit(
                    Resource.Error(
                        exception.message ?: "Error fetching data"
                    )
                )
            }
     }

    fun getResultList(id: String): Flow<Resource<List<ResultListResponseItem>>> = flow {
        emit(Resource.Loading())
        try {
            val data = apiService.getResultList(id)
            emit(Resource.Success(data))
            Log.d("DataRepository", "Data: $data")
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    exception.message ?: "Error fetching data"
                )
            )
            Log.d("DataRepository", "Error: ${exception.message}")
        }
    }

    fun login(username: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading()) // Emit loading state
        try {
            val response = apiService.login(username, password)
            if (response.status == true && response.data != null) {
                val token = response.data
                tokenManager.saveToken(token) // Save the token
                emit(Resource.Success(true)) // Emit success state
            } else {
                emit(Resource.Error("Login failed: ${response.message}")) // Emit error with message
            }
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    exception.message ?: "An error occurred during login"
                )
            )
        }
    }

    fun register(name: String, email: String, password: String, passwordRepeat: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.register(name, email, password, passwordRepeat)
            if (response.status == true) {
                emit(Resource.Success(true))
                } else {
                emit(Resource.Error("Registration failed: ${response.message}")) // Emit error with message
            }
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    exception.message ?: "An error occurred during registration"
                )
            )
        }
    }

    fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.logout("Bearer: ${tokenManager.getToken()}")
            if (response.status == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Logout failed: ${response.message}"))
            }
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    exception.message ?: "An error occurred during logout"
                )
            )
        }
    }
}