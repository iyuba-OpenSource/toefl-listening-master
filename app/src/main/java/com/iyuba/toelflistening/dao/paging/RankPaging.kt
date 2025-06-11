package com.iyuba.toelflistening.dao.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iyuba.toelflistening.Repository
import com.iyuba.toelflistening.bean.RankItem
import com.iyuba.toelflistening.utils.GlobalHome

/**
苏州爱语吧科技有限公司
 */
class RankPaging(val url:String):PagingSource<Int, RankItem>() {

    override fun getRefreshKey(state: PagingState<Int, RankItem>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RankItem> {
        return  try {
            val currentPage = params.key ?: 0
            val pageSize= params.loadSize
            val newData= Repository.getRankData(url,currentPage, pageSize)
            if (!newData.isNotLogin()){
                GlobalHome.rankResponse.copyChange(newData)
            }
            val nextPage = if (newData.data.size<pageSize) null else currentPage + 1
            LoadResult.Page(newData.data, null, nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}