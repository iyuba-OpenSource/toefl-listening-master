package com.iyuba.toelflistening.activity

import android.view.View
import androidx.core.content.ContextCompat
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.AnswerItem
import com.iyuba.toelflistening.bean.ExerciseRecord
import com.iyuba.toelflistening.bean.ExplainItem
import com.iyuba.toelflistening.databinding.ActivityQuestionResultBinding
import com.iyuba.toelflistening.utils.*

class QuestionResultActivity : BaseActivity<ActivityQuestionResultBinding>() ,View.OnClickListener{
    private lateinit var paresAnswer:String

    override fun ActivityQuestionResultBinding.initBinding() {
        setTitleText("练习结果")
        viewAnalysis.setOnClickListener(this@QuestionResultActivity)
        val result=intent.getParcelableExtra<ExerciseRecord>(ExtraKeyFactory.exerciseRecord)
        val answerItem=intent.getParcelableExtra< AnswerItem>(ExtraKeyFactory.answerItem)
        intent.getParcelableExtra<ExplainItem>(ExtraKeyFactory.currentExplain)?.let {
            val nextLine="\n"
            val text=if (it.explains.contains(nextLine)){
                it.explains.replace(nextLine,"")
            }else{
                it.explains
            }
            val green= ContextCompat.getColor(this@QuestionResultActivity, R.color.main)
            val proper=text.substring(text.indexOf("：")+1,text.indexOf("解"))
            val currentText=StringBuilder().apply {
                append("您的答案：")
                append(result?.UserAnswer)
            }
            if (result?.UserAnswer==proper){
                currentAnswer.setTextColor(green)
                currentText.append("正确")
            }else{
                val main=ContextCompat.getColor(this@QuestionResultActivity,R.color.purple_500)
                currentAnswer.setTextColor(main)
                currentText.append("错误")
            }
            result?.let { record->
                record.apply {
                    RightAnswer=proper
                    AnswerResut= if (proper==record.UserAnswer) "1" else "0"
                }
            }
            currentAnswer.text=currentText.toString()
            properAnswer.setTextColor(green)
            val properText="正确答案:$proper"
            properAnswer.text=properText
            val lineIndex=text.indexOf("解析")
            paresAnswer=text.substring(lineIndex,text.length)
        }
        answerItem?.let {
            this.title=it.quesText
            if (it.answer.split(OtherUtils.delimiter).size<=1){
                oldOptions.addRadioButton(this@QuestionResultActivity,it.answerText,false)
            }else{
                oldOptionsMany.addCheckItem(this@QuestionResultActivity,it.answerText,false)
            }
        }
        result?.let {
            it.run {
                UserAnswer
            }.apply {
                if (length<=1){
                    val index= OtherUtils.selectArray.indexOfFirst { response-> response==this }
                    oldOptions.check(index)
                }else{
                    val checkList=oldOptionsMany.getSingleCheck()
                    val array=toCharArray()
                    for (i in array.indices){
                        val index= OtherUtils.selectArray.indexOfFirst { item-> item==(array[i].toString()) }
                        checkList[index].isChecked=(index in checkList.indices)
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        judgeVip(getString(R.string.view_analysis)){
            paresAnswer.showDialog(this,method = {})
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        GlobalHome.exerciseResultBack=false
    }
}