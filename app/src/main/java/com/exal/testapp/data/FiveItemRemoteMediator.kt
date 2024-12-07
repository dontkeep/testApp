package com.exal.testapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.exal.testapp.data.local.AppDatabase
import com.exal.testapp.data.local.entity.ListEntity
import com.exal.testapp.data.local.entity.RemoteKeys
import com.exal.testapp.data.network.ApiServices

@OptIn(ExperimentalPagingApi::class)
class FiveItemRemoteMediator(
    private val type: String,
    private val token: String,
    private val apiService: ApiServices,
    private val database: AppDatabase
) : RemoteMediator<Int, ListEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> return MediatorResult.Success(endOfPaginationReached = true)

        }

        try {
            val responseData = apiService.getExpenseList("Bearer: $token", type, page, state.config.pageSize)

            val endOfPaginationReached = responseData.data?.lists.isNullOrEmpty()
            val lists = responseData.data?.lists?.take(5)?.mapNotNull { list ->
                list?.let {
                    ListEntity(
                        id = it.id?.toString()
                            ?: throw IllegalArgumentException("ID is required"),
                        title = it.title,
                        type = type,
                        totalExpenses = it.totalExpenses,
                        totalProducts = it.totalProducts,
                        totalItems = it.totalItems,
                        createdAt = it.createdAt,
                        boughtAt = it.boughtAt,
                        image = it.image
                    )
                }
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.listDao().clearByType(type)
                }

                val prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = responseData.data?.lists?.take(5)?.map {
                    RemoteKeys(
                        id = it?.id.toString(),
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                keys?.let { database.remoteKeysDao().insertAll(it) }
                lists?.let { database.listDao().insertAll(it) }
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysById(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysById(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id?.let { itemId ->
                database.remoteKeysDao().getRemoteKeysById(itemId)
            }
        }
    }
}
