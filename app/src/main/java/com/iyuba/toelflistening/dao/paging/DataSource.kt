package com.iyuba.toelflistening.dao.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iyuba.toelflistening.Repository
import com.iyuba.toelflistening.bean.StrangenessWordItem
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.clearSelf


/**
苏州爱语吧科技有限公司
 */
class DataSource :PagingSource<Int, StrangenessWordItem>() {
    private val map= mutableMapOf<String,String>()
    override fun getRefreshKey(state: PagingState<Int, StrangenessWordItem>): Int? =null
    override val keyReuseSupported: Boolean  = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StrangenessWordItem> {
        return  try {
            //页码未定义置为1
            val currentPage = params.key ?: 1
            val pageSize= params.loadSize
            //仓库层请求数据
            val reUrl="http://word.iyuba.cn/words/wordListService.jsp"
            map.clearSelf().apply {
                put("u",GlobalHome.userInfo.uid.toString())
                put("pageNumber",currentPage.toString())
                put("pageCounts",pageSize.toString())
            }
            val demoReqData = Repository.requestStrangenessWord(reUrl,map)
            GlobalHome.wordPageCounts=demoReqData.row.size
            val nextPage = if (demoReqData.totalPage==currentPage) null else currentPage + 1
            LoadResult.Page(demoReqData.row, null, nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}