package com.example.storyapp.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.network.ApiService
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val apiService: ApiService, private val preferenceManager: PreferenceManager): PagingSource<Int, ListStoryItem>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = preferenceManager.getSession().first().token
            val responseData = apiService.getAllStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1,
            )
        } catch (e : Exception) {
            return LoadResult.Error(e)
        }
    }
}