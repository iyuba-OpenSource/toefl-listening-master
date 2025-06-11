package com.iyuba.toelflistening.activity

import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.adapter.QuestionAdapter
import com.iyuba.toelflistening.bean.QuestionItem
import com.iyuba.toelflistening.databinding.ActivityQuestionTestBinding
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.startActivity
import kotlinx.coroutines.launch

/**
 * 试题列表
 */
class QuestionTestActivity : BaseActivity<ActivityQuestionTestBinding>(), Consumer<Int> {
    private val list = mutableListOf<QuestionItem>()

    override fun ActivityQuestionTestBinding.initBinding() {
        val toeflName = intent.getStringExtra(ExtraKeyFactory.toeflName)
        val questionAdapter = with(QuestionAdapter()) {
            actionListener = this@QuestionTestActivity
            this
        }
        binding.questionList.apply {
            adapter = questionAdapter
            addDefaultDecoration()
        }
        toeflName?.let { str ->
            setTitleText("$str 试题")
            lifecycleScope.launch {
                toeflViewModel.requestQuestList(str).collect {
                    list.addAll(it)
                    questionAdapter.changeData(list)
                }
            }
        }
        lifecycleScope.launch {
            toeflViewModel.downError.collect { result ->
                result.onSuccess {
                    dismissLoad()
                }.onLoading {
                    showLoad()
                }.onError {
                    dismissLoad()
                }
            }
        }
    }

    override fun accept(t: Int) {
        startActivity<QuestionInfoActivity> {
            putExtra(ExtraKeyFactory.questionInfo, list[t])
            putExtra(ExtraKeyFactory.questionPosition, t)
        }
    }

}