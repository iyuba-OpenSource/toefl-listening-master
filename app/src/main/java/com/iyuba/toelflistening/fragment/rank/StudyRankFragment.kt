package com.iyuba.toelflistening.fragment.rank

import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.adapter.StudyRankAdapter
import com.iyuba.toelflistening.bean.GroupRankItem
import com.iyuba.toelflistening.bean.StudyRankEvent
import com.iyuba.toelflistening.databinding.StudyRankLayoutBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.visibilityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
苏州爱语吧科技有限公司
@Date:  2023/1/16
@Author:  han rong cheng
 */
class StudyRankFragment(private val flag :Boolean=false): BaseFragment<StudyRankLayoutBinding>() {
    override fun StudyRankLayoutBinding.initBinding() {

        username=GlobalHome.userInfo.username
        val studyAdapter= with(StudyRankAdapter()){
            itemListener=listener
            this
        }
        studyRank.apply {
            adapter=studyAdapter
            addDefaultDecoration()
        }
        userAction.loadStudyListenRank(flag)
        lifecycleScope.launch {
            userAction.judgeErrorFlow(flag).collect{
                item=it
            }
        }
        lifecycleScope.launch {
            val result=userAction.judgeRankFlow(flag).first()
            result.onError {
                studyRank.visibilityState(true)
            }.onSuccess {
                studyEmpty.visibilityState(true)
                studyAdapter.submitData(it)
            }
        }
    }
    private val  listener=Consumer<GroupRankItem>{
//        requireActivity().gotoEvaluationInfo(it.uid,it.name,it.imgSrc)
    }

    override fun initEventBus(): Boolean =true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:StudyRankEvent){
        event.groupResponse.apply {
            bind.item=this
            lifecycleScope.launch {
                userAction.rankIndexFlow.emit(myranking)
            }
        }
    }
}