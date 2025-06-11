package com.iyuba.toelflistening.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.iyuba.imooclib.ui.mobclass.MobClassActivity
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.CorrectSoundResponse
import com.iyuba.toelflistening.bean.EvaluationSentenceData
import com.iyuba.toelflistening.bean.EvaluationSentenceDataItem
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.databinding.CorrectSoundLayoutBinding
import com.iyuba.toelflistening.utils.*
import com.iyuba.toelflistening.utils.logic.GlobalPlayManager
import com.iyuba.toelflistening.utils.logic.VoiceStatus
import com.iyuba.toelflistening.utils.view.LocalMediaRecorder
import com.iyuba.toelflistening.viewmodel.ToeflViewModel
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
 */
class CorrectSoundDialog : DialogFragment() {
    private val  fileName by lazy { "${requireContext().externalCacheDir?.absolutePath}audio_record_word.wav" }
    private val recorder by lazy { LocalMediaRecorder() }
    private lateinit var bind: CorrectSoundLayoutBinding
    private val finalList = mutableListOf<EvaluationSentenceDataItem>()
    private val evaluation by lazy { ViewModelProvider(requireActivity())[ToeflViewModel::class.java] }
    private var videoUrl = ""
    private var wordUrl=""
    private lateinit var groupItem: TextItem
    private lateinit var currentItem:EvaluationSentenceDataItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bind = DataBindingUtil.inflate(inflater, R.layout.correct_sound_layout, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.closeCorrectSound.setOnClickListener(clickListener)
        bind.playCorrect.setOnClickListener(clickListener)
        bind.listenOriginal.setOnClickListener(clickListener)
        bind.clickStart.setOnClickListener(clickListener)
        bind.seekLisa.setOnClickListener(clickListener)
        bind.wordScore.apply {
            visibility=View.INVISIBLE
            setOnClickListener(clickListener)
        }
        bind.contentEvaluation.setOnWordClickListener(onWordClick)
        lifecycleScope.launch {
            evaluation.lastEvalWord.collect{result->
                result.onError {
                    it.judgeType().showToast()
                }.onSuccess {
                    evalWordSuccess(it.data)
                }
            }
        }
        lifecycleScope.launch {
            evaluation.lastCorrectWord.collect{result->
                result.onError {
                    it.judgeType().showToast()
                }.onSuccess {
                    correctWordSuccess(it.first)
                    if (it.second){
                        playVideo()
                    }
                }
            }
        }
    }

    private fun correctWordSuccess(data:CorrectSoundResponse){
        bind.correctPronunciation.text = data.realOri
        bind.yourPronunciation.text = data.realUserPron
        val wordDefinition=resources.getString(R.string.word_definition)+ (data.def?:"暂无")
        bind.wordDefinition.text=wordDefinition
        bind.playCorrect.visibility= when {
            data.audio==null -> View.GONE
            data.audio.isNotEmpty() -> {
                videoUrl = data.audio
                View.VISIBLE
            }
            else -> View.GONE
        }
    }

    private fun evalWordSuccess(data:EvaluationSentenceData){
        val item=data.words[0]
        wordUrl=data.URL
        evaluation.updateEvaluationChildStatus(item.score,groupItem.onlyKay,currentItem.index)
        bind.wordScore.apply {
            visibility=View.VISIBLE
            text=data.realScopes
        }
    }

    fun changeContent(bean: TextItem,word:String) {
        val map = GlobalHome.evaluationMap
        val sumList = mutableListOf<EvaluationSentenceDataItem>()
        map.values.forEach { sumList.addAll(it) }
        groupItem=bean
        finalList.clear()
        finalList.addAll(sumList.filter { it.onlyKay == bean.onlyKay })
        bind.contentEvaluation.apply {
            text = map.showSpannable(bean.onlyKay)
            val selectTextColor= ContextCompat.getColor(requireContext(),R.color.bookChooseUncheck)
            setSelectTextBackColor(selectTextColor)
        }
        if (finalList.isNotEmpty()) {
            bind.title = word
            currentItem = finalList.filter { it.content.contains(word) }[0]
            evaluation.correctSound(word, currentItem,false)
        }
    }

    private fun playVideo(url:String=videoUrl) {
        if (url.isEmpty()){
            return
        }
        GlobalPlayManager.addUrl(Pair(VoiceStatus.EVAL,url))
    }

    private val clickListener= View.OnClickListener {
        when (it.id) {
            R.id.close_correct_sound -> dismiss()
            R.id.play_correct -> playVideo()
            R.id.listen_original -> playVideo()
            R.id.word_score -> playVideo(wordUrl.changeVideoUrl())
            R.id.click_start -> clickStart()
            R.id.seek_lisa->{
                val ownerId=3
                val typeIdFilter= ArrayList<Int>()
                typeIdFilter.add(ownerId)
                startActivity(MobClassActivity.buildIntent(activity, ownerId, true, typeIdFilter))
            }
        }
    }

    private fun clickStart(){
        bind.clickStart.text=if (recorder.isRecording()) {
            "结束评测".showToast()
            stopRecord()
            resources.getString(R.string.click_start)
        } else {
            recorder.startPrepare(fileName,lifecycleScope)
            "开始评测".showToast()
            resources.getString(R.string.click_stop)
        }
    }

    private fun stopRecord() {
        if (recorder.isRecording()) {
            recorder.stopRecord()
            val index=currentItem.index.toString()
            evaluation.evaluationWord(groupItem,fileName,index)
        }
    }

    private val  onWordClick=object : OnWordClickListener() {
        override fun onNoDoubleClick(str: String) {
            bind.title = str
            if (finalList.isNotEmpty()) {
                currentItem = finalList.filter { it.content.contains(str) }[0]
                evaluation.correctSound(str, currentItem,true)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dw = dialog?.window
        dw!!.setBackgroundDrawableResource(R.drawable.bg_black_round_10dp) //一定要设置背景
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val params = dw.attributes
        //屏幕底部显示
        params.gravity = Gravity.CENTER
        //设置屏幕宽度高度
        params.width = (dm.widthPixels / 1.1f).toInt()//屏幕宽度
        params.height = (dm.heightPixels / 1.5f).toInt() //屏幕高度的1/3
        dw.attributes = params
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalPlayManager.executeDestroy()
    }
}