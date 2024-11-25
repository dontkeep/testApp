package com.exal.testapp.data.network

import androidx.lifecycle.LiveData
import com.exal.testapp.data.network.response.ExpenseListResponse
import com.exal.testapp.data.network.response.ExpenseListResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiServices {
    @GET("/expense-lists")
    suspend fun getExpensesList(@Query("id_user") id: String): List<ExpenseListResponseItem>
}