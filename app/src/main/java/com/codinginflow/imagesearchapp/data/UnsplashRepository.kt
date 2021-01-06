package com.codinginflow.imagesearchapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.codinginflow.imagesearchapp.api.UnsplashApi
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val UnsplashAPi :UnsplashApi) {

    fun getSearchResults (query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,

            ),
            pagingSourceFactory = {UnsplashPagingSourse(UnsplashAPi,query)}
        ).liveData
}