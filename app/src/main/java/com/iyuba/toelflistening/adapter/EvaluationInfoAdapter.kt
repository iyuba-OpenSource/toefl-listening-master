package com.iyuba.toelflistening.adapter

import android.graphics.Color
import android.util.Log
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.RankInfoItem
import com.iyuba.toelflistening.databinding.EvaluationInfoItemBinding
import com.iyuba.toelflistening.utils.OnEvaluationInfoOperateListener
import com.iyuba.toelflistening.utils.changeVideoUrl

/**
苏州爱语吧科技有限公司
 */
class EvaluationInfoAdapter :BaseAdapter<RankInfoItem, EvaluationInfoItemBinding>(){
    lateinit var itemListener: OnEvaluationInfoOperateListener
    override fun EvaluationInfoItemBinding.onBindViewHolder(bean: RankInfoItem, position: Int) {
        item=bean
        evaluationType.apply {
            when(bean.shuoshuotype){
                2-> {
                    setBackgroundResource(R.drawable.rank_border)
                    setTextColor(Color.WHITE)
                }
                4-> {
                    setBackgroundResource(R.drawable.border)
                    setTextColor(Color.BLACK)
                }
            }
        }

        playEvaluation.setOnClickListener {
            itemListener.playVideo(bean.ShuoShuo.changeVideoUrl())
        }
        likeEvaluation.setOnClickListener {
            itemListener.likeItem(bean)
        }
    }
}