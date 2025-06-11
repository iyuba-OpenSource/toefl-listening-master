package com.iyuba.toelflistening.adapter

import androidx.core.util.Consumer
import com.iyuba.toelflistening.bean.ExerciseRecord
import com.iyuba.toelflistening.databinding.SeekAnalysisItemBinding

/**
苏州爱语吧科技有限公司
@Date:  2023/1/31
@Author:  han rong cheng
 */
class AnalysisInfoAdapter:BaseAdapter<ExerciseRecord,SeekAnalysisItemBinding>() {
    private lateinit var  seekAnalysis:Consumer<String>
    override fun SeekAnalysisItemBinding.onBindViewHolder(bean: ExerciseRecord, position: Int) {
        item=bean
        analysisSeek.setOnClickListener {
            if (::seekAnalysis.isInitialized){
                seekAnalysis.accept(bean.analysis)
            }
        }
    }

    fun inflaterClickListen(listener:Consumer<String>){
        seekAnalysis=listener
    }
}