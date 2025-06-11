package com.iyuba.toelflistening.adapter

import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.databinding.EvaluationItemLayoutBinding
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OnEvaluationListener
import com.iyuba.toelflistening.utils.OnWordClickListener
import com.iyuba.toelflistening.utils.showSpannable
import com.iyuba.toelflistening.utils.view.ControlVideoProgressView

/**
苏州爱语吧科技有限公司
 */
class EvaluationSentenceAdapter : BaseAdapter<TextItem, EvaluationItemLayoutBinding>() {

    lateinit var evalListener: OnEvaluationListener
    val videoList = mutableListOf<ControlVideoProgressView>()

    override fun EvaluationItemLayoutBinding.onBindViewHolder(bean: TextItem, position: Int) {
        item = bean
        if (bean.success) {
            sentenceItem.text = GlobalHome.evaluationMap.showSpannable(bean.onlyKay)
        } else {
            sentenceItem.text = bean.sentence
        }
        sentenceItem.apply {
            setOnWordClickListener(object : OnWordClickListener() {
                override fun onNoDoubleClick(str: String) {
                    evalListener.searchWord(str, this@apply, bean.success, bean)
                }
            })
        }
        root.setOnClickListener {
            evalListener.showOperate(position)
        }
        controlProgress.apply {

            val start = if (position == 0) 0 else data[position].timing
            val end = (if (position == data.size - 1) -1 else data[position + 1].timing).toFloat()
            injectVideoUrl(GlobalHome.videoUrl, start, end)
            videoList.add(this)
            setControlVideoProgressView(videoList)
        }
        mike.setOnClickListener {
            evalListener.openMike(position, mike)
        }
        controlSelf.apply {
            setOnClickListener { evalListener.playSelf(position, this) }
        }
        releaseItem.setOnClickListener {
            evalListener.releaseSimple(bean)
        }
        correctSound.setOnClickListener {

        }
    }
}