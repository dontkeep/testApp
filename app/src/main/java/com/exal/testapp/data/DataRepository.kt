package com.exal.testapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.exal.testapp.data.local.AppDatabase
import com.exal.testapp.data.local.entity.ListEntity
import com.exal.testapp.data.network.ApiServices
import com.exal.testapp.data.network.response.DetailListResponse
import com.exal.testapp.data.network.response.ExpenseListResponseItem
import com.exal.testapp.data.network.response.GetListResponse
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
class DataRepository @Inject constructor(
    @RegularApiService private val apiService: ApiServices,
    @MlApiService private val apiServiceML: ApiServices,
    private val tokenManager: TokenManager,
    private val database: AppDatabase
) {

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

    fun register(
        name: String,
        email: String,
        password: String,
        passwordRepeat: String
    ): Flow<Resource<Boolean>> = flow {
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
        totalExpenses: RequestBody,
        totalItems: RequestBody
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
                totalExpenses,
                totalItems
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error posting data"))
        }
    }

    fun getExpenseList(): Flow<Resource<GetListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getExpenseList("Bearer: ${tokenManager.getToken()}")
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    e.message ?: "Error fetching expense list"
                )
            ) // Emit error if something goes wrong
        }
    }

    fun getExpensesDetail(id: Int): Flow<Resource<DetailListResponse>> = flow {
        emit(Resource.Loading())
        try {
//            val response = apiService.getExpensesDetail("Bearer: ${tokenManager.getToken()}", id)
//            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message ?: "Error fetching data"))
        }
    }


    @OptIn(ExperimentalPagingApi::class)
    fun getListData(type: String): Flow<PagingData<ListEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = ListRemoteMediator(
                type = type,
                token = "${tokenManager.getToken()}",
                apiService = apiService,
                database = database
            ),
            pagingSourceFactory = { database.listDao().getListsByType(type) }
        ).flow
    }

    fun deleteAllDatabaseData(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            database.listDao().clearAll()
            database.remoteKeysDao().clearRemoteKeys()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error deleting data"))
        }
    }
}