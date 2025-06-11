package com.iyuba.toelflistening.activity

import android.media.MediaPlayer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.iyuba.toelflistening.adapter.EvaluationInfoAdapter
import com.iyuba.toelflistening.bean.LikeEvaluation
import com.iyuba.toelflistening.bean.RankInfoItem
import com.iyuba.toelflistening.databinding.ActivityEvaluationInfoBinding
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EvaluationInfoActivity : BaseActivity<ActivityEvaluationInfoBinding>(), OnEvaluationInfoOperateListener{
    private val list= mutableListOf<RankInfoItem>()
    private lateinit var player: MediaPlayer
    private lateinit var infoAdapter: EvaluationInfoAdapter
    override fun ActivityEvaluationInfoBinding.initBinding() {
        val userId=intent.getIntExtra(ExtraKeyFactory.userId,0)
        val userName=intent.getStringExtra(ExtraKeyFactory.userName)
        infoAdapter= EvaluationInfoAdapter()
        infoAdapter.itemListener=this@EvaluationInfoActivity
        evaluationInfo.apply {
            adapter=infoAdapter
            val decoration= DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
        }
        setTitleText("\"${userName}\"的评测")
        lifecycleScope.launch {
            val result=toeflViewModel.getWorksByUserId(userId).first().data
            result.forEach {
                val item=if (it.shuoshuotype==4){
                    //合成
                    toeflViewModel.selectSimpleEvaluation()
                }else{
                    //单句
                    try {
                        toeflViewModel.selectSimpleEvaluation(it.idIndex)
                    }catch (e:IndexOutOfBoundsException){
                        toeflViewModel.selectSimpleEvaluation()
                    }
                }.first()
                it.apply {
                    username= userName.toString()
                    sentenceZh=item.sentence
                    headUrl=intent.getStringExtra(ExtraKeyFactory.userHead).toString()
                }

            }
            list.addAll(result)
            infoAdapter.changeData(result)
        }
    }



    override fun playVideo(url: String) {
        if (!::player.isInitialized){
            player=MediaPlayer()
        }
        if (!player.isPlaying){
            player.apply {
                reset()
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener{
                    "开始播放".showToast()
                    start()
                }
                setOnCompletionListener { "播放结束".showToast() }
            }
        }
    }

    override fun likeItem(item: RankInfoItem) {
        //不宜协程套协程
        if (!GlobalHome.isLogin()){
            showGoLoginDialog()
            return
        }
        lifecycleScope.launch {
            val local=toeflViewModel.selectSimpleLikeEvaluation(item.id).first()
            if (local.isEmpty()){
                val netResult=toeflViewModel.likeEvaluation(item.id).first()
                ("点赞"+if (netResult.isNotEmpty()) {
                    val index=list.indexOfFirst { it==item }
                    list[index].agreeCount=list[index].agreeCount.inc()
                    infoAdapter.notifyItemChanged(index)
                    val like= LikeEvaluation(GlobalHome.userInfo.uid,item.id)
                    toeflViewModel.insertSimpleLikeEvaluation(like).first()
                    "成功"
                } else "失败").showToast()
            }else{
                "不能重复点赞".showToast()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        if (!::player.isInitialized){
            return
        }
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!::player.isInitialized){
            return
        }
        runCatching {
            player.apply {
                stop()
                reset()
                release()
            }
        }
    }
}