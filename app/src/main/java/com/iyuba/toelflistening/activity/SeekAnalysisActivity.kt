package com.iyuba.toelflistening.activity

import androidx.core.util.Consumer
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.adapter.AnalysisInfoAdapter
import com.iyuba.toelflistening.bean.ExerciseRecord
import com.iyuba.toelflistening.databinding.ActivitySeekAnalysisBinding
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.judgeVip
import com.iyuba.toelflistening.utils.showDialog

class SeekAnalysisActivity : BaseActivity<ActivitySeekAnalysisBinding>() {
    private val title by lazy { getString(R.string.view_analysis) }

    override fun ActivitySeekAnalysisBinding.initBinding() {
        setTitleText(title)
        val list= intent.getParcelableArrayListExtra<ExerciseRecord>(ExtraKeyFactory.viewAnalysisList) ?: return
        val dataAdapter= with(AnalysisInfoAdapter()){
            inflaterClickListen(listener)
            changeData(list)
            this
        }
        analysisList.apply {
            addDefaultDecoration()
            adapter=dataAdapter
        }
    }

    private val listener= Consumer<String> {
        judgeVip(title){
            it.showDialog(this,method = {})
        }
    }
}