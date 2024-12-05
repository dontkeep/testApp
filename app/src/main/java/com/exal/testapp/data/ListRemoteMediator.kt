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
class ListRemoteMediator(
    private val type: String,
    private val token: String,
    private val apiService: ApiServices,
    private val database: AppDatabase
) : RemoteMediator<Int, ListEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = apiService.getExpenseList(
                token = "Bearer: $token",
                type = type,
                page = page,
                limit = state.config.pageSize
            )

            val lists = response.data?.lists?.mapNotNull { list ->
                list?.let {
                    ListEntity(
                        id = it.id?.toString() ?: throw IllegalArgumentException("ID is required"), // Default ke "" jika null
                        title = it.title, // Default title jika null
                        type = type,
                        totalExpenses = it.totalExpenses,
                        totalProducts = it.totalProducts,
                        totalItems = it.totalItems,
                        createdAt = it.createdAt,
                        image = it.image
                    )
                }
            }

            Log.d("RemoteMediator", "Fetched ${lists?.size} items for page $page")

            val endOfPaginationReached = lists.isNullOrEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.listDao().clearByType(type)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = lists!!.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.listDao().insertAll(lists)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysById(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysById(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                database.remoteKeysDao().getRemoteKeysById(repoId)
            }
        }
    }
}
