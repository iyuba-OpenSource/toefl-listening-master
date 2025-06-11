package com.iyuba.toelflistening.activity

import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.iyuba.toelflistening.adapter.PagerAdapter
import com.iyuba.toelflistening.bean.QuestionDetailItem
import com.iyuba.toelflistening.bean.QuestionItem
import com.iyuba.toelflistening.databinding.ActivityQuestionInfoBinding
import com.iyuba.toelflistening.fragment.toelf.EvaluationFragment
import com.iyuba.toelflistening.fragment.toelf.OriginalFragment
import com.iyuba.toelflistening.fragment.toelf.QuestionFragment
import com.iyuba.toelflistening.fragment.toelf.RankFragment
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.getSpaceCount
import com.iyuba.toelflistening.utils.replaceOtherSpace
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuestionInfoActivity : BaseActivity<ActivityQuestionInfoBinding>() {
    private lateinit var mediator: TabLayoutMediator
    private var position = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun ActivityQuestionInfoBinding.initBinding() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val result = intent.getSerializableExtra(ExtraKeyFactory.questionInfo) as QuestionItem
        position = intent.getIntExtra(ExtraKeyFactory.questionPosition, 0)
        val frameArray = with(mutableListOf<Fragment>()) {
            add(OriginalFragment())
            add(QuestionFragment())
            add(EvaluationFragment())
            add(RankFragment())
            this
        }
        val titleArray = with(mutableListOf<String>()) {
            add("听力原文")
            add("问题")
            add("评测")
            add("排行")
            forEach {
                headTab.addTab(headTab.newTab().setText(it))
            }
            this
        }
        questionPager.adapter = PagerAdapter(this@QuestionInfoActivity, frameArray)
        mediator = TabLayoutMediator(headTab, questionPager, true) { tab, position ->
            tab.text = titleArray[position]
        }
        mediator.attach()

        //请求数据，上面先订阅，在请求数据
        resultLet(result)
    }

    override fun onDestroy() {
        mediator.detach()
        super.onDestroy()
    }

    private fun resultLet(result: QuestionItem) {
        toeflViewModel.transferQuestion(result)
        val userId = GlobalHome.userInfo.uid
        setTitleText(result.titleName)
        lifecycleScope.launch {
            val type = result.titleName.split(" ")[0]
            val netData = toeflViewModel.requestQuestDetail(type).catch { }.first().itemList
            val sentenceList = toeflViewModel.selectSentenceList(userId, result.titleNum).catch { }.first()
            netData.forEach {
                val titleName =
                    if (result.titleName.getSpaceCount() == it.titleIntro[0].titleName.getSpaceCount()) {
                        result.titleName
                    } else {
                        result.titleName.replaceOtherSpace().replaceOtherSpace()
                    }
                if (titleName == it.titleIntro[0].titleName) {
                    it.textList = if (sentenceList.isEmpty()) {
                        val textList = it.textList
                        textList.forEach { item -> item.inflateEmpty(type) }
                        toeflViewModel.insertSentence(textList).catch {}.first()
                        textList
                    } else {
                        val evaluationList = toeflViewModel.selectEvaluationList(
                            userId,
                            sentenceList[0].titleNum.toInt()
                        ).catch {}.first()
                        toeflViewModel.transferEvaluationList(evaluationList)
                        sentenceList
                    }
                    transferQuestionDetailItem(it)
                }
            }
        }
    }

    private suspend fun transferQuestionDetailItem(item: QuestionDetailItem) {
        toeflViewModel.apply {
            transferMainVideo(item.sound)
            if (item.titleIntro.isNotEmpty()) {
                val simple = item.titleIntro[0]
                transferTitleIntroItem(simple)
                GlobalHome.titleNum = simple.titleNum.toInt()
            }
            transferTextList(item.textList)
            //
            val number = item.answer[0].titleNum
//            requestTestRecord(number)
//            if (selectByNumber(number).catch { }.first()) {
//                requestTestRecord(number)
//            }
            transferAnswer(item.answer)
            transferExplain(item.explain)
        }
    }

}