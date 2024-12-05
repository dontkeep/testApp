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

        return try {
            val response = apiService.getExpenseList(
                token = "Bearer $token",
                type = type,
                page = page,
                limit = state.config.pageSize
            )

            val lists = response.data?.lists?.mapNotNull { list ->
                list?.let {
                    ListEntity(
                        id = it.id?.toString() ?: "", // Default ke "" jika null
                        title = it.title ?: "Untitled", // Default title jika null
                        type = type,
                        totalExpenses = it.totalExpenses ?: "0",
                        totalProducts = it.totalProducts ?: 0,
                        totalItems = it.totalItems ?: 0,
                        createdAt = it.createdAt ?: "Unknown Date",
                        image = it.image ?: ""
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

                val keys = lists?.map {
                    RemoteKeys(
                        id = it.id ?: "", // Gunakan default "" jika id null
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }


                if (!keys.isNullOrEmpty()) {
                    database.remoteKeysDao().insertAll(keys)
                }

                if (!lists.isNullOrEmpty()) {
                    database.listDao().insertAll(lists)
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysById(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysById(it) }
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
