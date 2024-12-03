package com.exal.testapp.data

import android.util.Log
import com.exal.testapp.data.network.ApiServices
import com.exal.testapp.data.network.response.ExpenseListResponseItem
import com.exal.testapp.data.network.response.PostListResponse
import com.exal.testapp.data.network.response.ResultListResponseItem
import com.exal.testapp.data.network.response.ScanImageResponse
import com.exal.testapp.data.request.ProductItem
import com.exal.testapp.helper.hilt.MlApiService
import com.exal.testapp.helper.hilt.RegularApiService
import com.exal.testapp.helper.manager.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(@RegularApiService private val apiService: ApiServices, @MlApiService private val apiServiceML: ApiServices, private val tokenManager: TokenManager) {
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

    fun scanImage(file: MultipartBody.Part): Flow<Resource<ScanImageResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiServiceML.scanImage(file)

            if (response.products?.isNotEmpty() == true) {
                emit(Resource.Success(response))
                Log.d("DataRepository", "Data: $response")
            } else {
                emit(Resource.Error("No products found in the response"))
            }
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message ?: "An error occurred during image scanning"))
        }
    }

    fun postData(
        title: RequestBody,
        receiptImage: MultipartBody.Part?,
        thumbnailImage: MultipartBody.Part?,
        productItems: RequestBody,
        type: RequestBody,
        totalExpenses: RequestBody
    ): Flow<Resource<PostListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.postData(
                "Bearer: ${tokenManager.getToken()}",
                title,
                receiptImage,
                thumbnailImage,
                productItems,
                type,
                totalExpenses
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error posting data"))
        }
    }
}