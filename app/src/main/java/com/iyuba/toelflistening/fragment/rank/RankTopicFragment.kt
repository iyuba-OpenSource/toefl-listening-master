package com.iyuba.toelflistening.fragment.rank

import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.iyuba.toelflistening.adapter.RankPagingAdapter
import com.iyuba.toelflistening.bean.RankItem
import com.iyuba.toelflistening.databinding.RankChildLayoutBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.visibilityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
@Date:  2023/1/13
@Author:  han rong cheng
 */
class RankTopicFragment(private val flag:Boolean): BaseFragment<RankChildLayoutBinding>() {
    override fun RankChildLayoutBinding.initBinding() {
        val rankAdapter= with(RankPagingAdapter()){
            itemListener=itemClickListener
            this
        }
        rankGroup.apply {
            adapter=rankAdapter
            addDefaultDecoration()
        }
        userAction.loadTopicRank(flag)
        lifecycleScope.launch {
            userAction.judgeTopicRankIn(flag).first().onError {
                rankGroup.visibilityState(true)
            }.onSuccess {
                topicRankEmpty.visibilityState(true)
                rankAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            userAction.judgeTopicErrorIn(flag).collect{
                it.testFlag=!flag
                item=it
            }
        }
        rankAdapter.addLoadStateListener { state->
            val notLoading=state.refresh is LoadState.NotLoading
            if (notLoading){
                lifecycleScope.launch {
                    GlobalHome.rankTopicResponse.apply {
                        testFlag=!flag
                        item= this
                        userAction.rankIndexFlow.emit(this.myranking)
                    }
                }
            }
        }
    }

    private val itemClickListener= Consumer<RankItem> {
//        requireActivity().gotoEvaluationInfo(it.uid,it.name,it.imgSrc)
    }

}