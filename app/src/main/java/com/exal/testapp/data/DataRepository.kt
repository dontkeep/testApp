package com.exal.testapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.exal.testapp.data.network.ApiServices
import com.exal.testapp.data.network.response.ExpenseListResponse
import com.exal.testapp.data.network.response.ExpenseListResponseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val apiService: ApiServices) {
     fun getExpensesList(id: String): Flow<Resource<List<ExpenseListResponseItem>>> =
        flow {
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
}