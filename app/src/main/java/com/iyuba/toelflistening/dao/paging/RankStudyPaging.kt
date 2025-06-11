package com.iyuba.toelflistening.dao.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iyuba.toelflistening.Repository
import com.iyuba.toelflistening.bean.GroupRankItem
import com.iyuba.toelflistening.bean.GroupRankResponse
import com.iyuba.toelflistening.bean.StudyRankEvent
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.putUserId
import com.iyuba.toelflistening.utils.signDate
import com.iyuba.toelflistening.utils.toMd5
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
@Date:  2023/1/16
@Author:  han rong cheng
 */
class RankStudyPaging(private val flag:Boolean): PagingSource<Int, GroupRankItem>() {
    private val dataMap= mutableMapOf<String,String>()
    private val type="D"
    override fun getRefreshKey(state: PagingState<Int, GroupRankItem>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroupRankItem> {
        return try {
            val start = params.key ?: 0
            val total= params.loadSize
            val date= signDate()
            val response=getStudyListenRanking(start.toString(), total.toString(), date,flag)
            val nextPage=(if (response.data.size< total) null else (start +1)*total)
            EventBus.getDefault().post(StudyRankEvent(response))
            LoadResult.Page(response.data,null,nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    /**
     * 获取(学习||听力)排行
     * */
    private suspend fun getStudyListenRanking(start:String,total:String,date: String,flag:Boolean=false): GroupRankResponse {
        val sign=with(StringBuilder()){
            append(GlobalHome.userInfo.uid)
            append(type)
            append(start)
            append(total)
            append(date)
            toString().toMd5()
        }
        val mode=(if (flag) "all" else "listening")
        dataMap.apply {
            clear()
            putUserId("uid")
            put("type",type)
            put("start",start)
            put("total",total)
            put("sign",sign)
            put("mode",mode)
        }
        return Repository.getStudyListenRanking(GlobalHome.studyRankUrl,dataMap)
    }
}