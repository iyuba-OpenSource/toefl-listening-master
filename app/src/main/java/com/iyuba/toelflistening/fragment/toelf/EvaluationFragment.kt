package com.iyuba.toelflistening.fragment.toelf

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.MemberCentreActivity
import com.iyuba.toelflistening.activity.QuestionInfoActivity
import com.iyuba.toelflistening.adapter.EvaluationSentenceAdapter
import com.iyuba.toelflistening.bean.MergeResponse
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.databinding.EvaluationLayoutBinding
import com.iyuba.toelflistening.dialog.CorrectSoundDialog
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OnEvaluationListener
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.changeTimeToString
import com.iyuba.toelflistening.utils.changeVideoUrl
import com.iyuba.toelflistening.utils.checkObtainPermission
import com.iyuba.toelflistening.utils.checkPermission
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.logic.GlobalPlayManager
import com.iyuba.toelflistening.utils.logic.VoiceStatus
import com.iyuba.toelflistening.utils.netEnabled
import com.iyuba.toelflistening.utils.showDialog
import com.iyuba.toelflistening.utils.showDialogEval
import com.iyuba.toelflistening.utils.showGoLoginDialog
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.startShowWordActivity
import com.iyuba.toelflistening.utils.timeStampDate
import com.iyuba.toelflistening.utils.view.RoundProgressBar
import com.iyuba.toelflistening.utils.view.SelectableTextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

/**
 *评测界面
 */
class EvaluationFragment : BaseFragment<EvaluationLayoutBinding>() {
    private lateinit var evalAdapter: EvaluationSentenceAdapter
    private lateinit var currentRecorder: ImageView

    companion object {
        //设置成静态的变量，实属无奈。主要给ControlVideoProgressView类的videoClick方法，用来判断是否在录音的状态
        @JvmStatic
        var currentPosition = -1
    }

    private var isRecording = false
    private val list = mutableListOf<TextItem>()
    private val recorder by lazy { MediaRecorder() }
    private val fileName by lazy { "${requireContext().externalCacheDir?.absolutePath}audio_record_toefl.wav" }
    private val player = MediaPlayer()
    private val timer = Timer(true)
    private lateinit var mergeResult: MergeResponse
    private val dialog by lazy { CorrectSoundDialog() }
    private val requestPermissionLaunch =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.checkPermission { showFirstHint() }
        }

    override fun EvaluationLayoutBinding.initBinding() {
        dismissActivityLoad<QuestionInfoActivity>()
        toeflViewModel.transferTextList.observe(requireActivity()) {
            list.addAll(it)
            evalAdapter = with(EvaluationSentenceAdapter()) {
                evalListener = this@EvaluationFragment.evalListener
                changeData(it)
                refreshSentence()
                this
            }
            evaluationList.apply {
                adapter = evalAdapter
                addDefaultDecoration()
            }
        }
        controlMerge.setOnClickListener(viewClickListener)
        synthesis.setOnClickListener(viewClickListener)
        release.setOnClickListener(viewClickListener)
        lifecycleScope.launch {
            GlobalPlayManager.statusOutFlow.collect { result ->
                result.onReady {
                    if (it == VoiceStatus.EVAL_MERGE) {
                        listenMergePrepared()
                    }
                }.onEnded {

                }.onIsPlaying {
                    bind.controlMerge.setBackgroundResource(R.drawable.pause_evaluation_old)
                }.onNotPlaying {
                    bind.controlMerge.setBackgroundResource(R.drawable.play_evaluation_old)
                }
            }
        }
        //排行榜的分享，“某些”点击限制，手动看提交记录返回
    }

    private fun listenMergePrepared() {
        val globalDuration = GlobalPlayManager.getDuration().toInt()
        bind.sumTime.text = globalDuration.changeTimeToString()
        bind.evaluationSeek.max = GlobalPlayManager.getDuration().toInt()
        lifecycleScope.launch {
            flow {
                while (true) {
                    delay(20)
                    emit(0)
                }
            }.collect {
                val current = GlobalPlayManager.getCurrentPosition().toInt()
                bind.nowTime.text = current.changeTimeToString()
                bind.evaluationSeek.progress = current
            }
        }
    }

    private fun refreshSentence() {
        toeflViewModel.transferEvaluationListResult.observe(requireActivity()) { childList ->
            list.forEach { item ->
                GlobalHome.evaluationMap[item.senIndex.toInt()] =
                    childList.filter { it.onlyKay == item.onlyKay }
            }
        }
        /**
         * 如何以一个正确的合适的方式用本地数据刷新TextView的颜色?,回调adapter不可行，adapter回调本界面不可取
         * 根据相同的onlyKey从父list里找到index，然后根据index刷新adapter，怎么刷新？何时刷新？？？
         * 为了安全，只能在获取父list后才能获取子list，现在只能嵌套liveData
         *
         * 最后只能向全局变量妥协
         * */
    }

    private fun showFirstHint() {
        if (!GlobalHome.isLogin()) {
            activity?.showGoLoginDialog()
            return
        }
        val synthesisLimit = 3
        val successLength = list.filter { it.success }.size
        val indexOut = successLength >= synthesisLimit
        if (!GlobalHome.userInfo.isVip() && indexOut) {
            "本篇你已评测3句！成为VIP后可评测更多".showDialog(requireContext()) {
                val i = Intent(requireContext(), MemberCentreActivity::class.java)
                startActivity(i)
            }
            return
        }
        if (!requireContext().netEnabled()) {
            "请先连接网络！".showDialog(requireContext()) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
            return
        }
        if (GlobalHome.showEvaluationHint) {
            "再次点击即可停止录音，完成评测".showDialogEval(requireContext(), "确定", "取消", {
                currentPosition = -1//取消则置为-1
            }, {
                startRecording()
            })
            GlobalHome.showEvaluationHint = false
        } else {
            startRecording()
        }
    }

    private fun stopRecord() {
        if (isRecording) {
            isRecording = false
            recorder.apply {
                setOnErrorListener(null)
                setOnInfoListener(null)
                setPreviewDisplay(null)
                stop()
                reset()
            }
        }
        val item = list[currentPosition]
        lifecycleScope.launch {
            try {
                val response = toeflViewModel.evaluationSentence(item, fileName).catch {

                    Timber.d("catch")
                }.first().data
                var onlyKey = UUID.randomUUID().toString()

                //Gson对于kotlin也不是很兼容
                if (item.onlyKay == null || item.onlyKay.isEmpty()) {
                    item.onlyKay = onlyKey
                } else {
                    onlyKey = item.onlyKay
                }
                item.apply {
                    success = true
                    fraction = (response.total_score * 20).toInt().toString()
                    selfVideoUrl = response.URL
                    userId = GlobalHome.userInfo.uid
                }
                //更新父状态
                toeflViewModel.updateEvaluationSentenceItemStatus(item).catch {

                    Timber.d("catch")
                }.first()
                val resultList = toeflViewModel.selectEvaluationByKey(onlyKey).catch {

                    Timber.d("catch")
                }.first()
                if (resultList.isNotEmpty()) {
                    toeflViewModel.deleteSentenceDataItemByKey(onlyKey)
                }
                //
                response.words.forEach {
                    it.apply {
                        userId = GlobalHome.userInfo.uid
                        onlyKay = onlyKey
                        index = item.senIndex.toInt()
                        groupNum = item.titleNum.toInt()
                    }
                }
                GlobalHome.evaluationMap[item.senIndex.toInt()] = response.words
                /**
                 * 根据EvaluationSentenceDataItem的去重-------------->HashMap简单粗暴
                 * */
                toeflViewModel.insertEvaluation(response.words)
                evalAdapter.notifyItemChanged(currentPosition)
                dismissActivityLoad<QuestionInfoActivity>()
                currentPosition = -1//currentPosition置为-1为没有操作的对象，可以播放或者评测
            } catch (e: Exception) {
                dismissActivityLoad<QuestionInfoActivity>()
                e.judgeType().showToast()
                currentPosition = -1//currentPosition置为-1为没有操作的对象，可以播放或者评测
            }
        }
    }

    private fun startRecording() {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            prepare()
            start()
            isRecording = true
        }
        currentRecorder.setBackgroundResource(R.drawable.mike_red)
    }

    override fun onPause() {
        super.onPause()
        evalAdapter.videoList.forEach { it.pausePlayer() }
    }

    override fun onDestroy() {
        super.onDestroy()
        evalAdapter.videoList.forEach { it.releasePlayer() }
        timer.cancel()
    }

    private val viewClickListener = View.OnClickListener {
        when (it.id) {
            R.id.synthesis -> synthesisVideos()
            R.id.control_merge -> controlMerge()
            R.id.release -> releaseMergeResult()
        }
    }

    private fun controlMerge() {
        val back = if (GlobalPlayManager.isPlaying()) {
            GlobalPlayManager.pause()
            R.drawable.play_evaluation_old
        } else {
            if (GlobalPlayManager.isEnd()) {
                val pair = Pair(VoiceStatus.EVAL_MERGE, mergeResult.URL.changeVideoUrl())
                GlobalPlayManager.addUrl(pair)
                bind.evaluationSeek.max = GlobalPlayManager.getDuration().toInt()
            } else {
                GlobalPlayManager.start()
            }
            R.drawable.pause_evaluation_old
        }
        bind.controlMerge.setBackgroundResource(back)
    }

    private fun releaseMergeResult() {
        lifecycleScope.launch {
            val score = bind.synthesisScore.text.toString()
            val voaId = list.first().titleNum.toInt()
            toeflViewModel.releaseMerge(score, mergeResult.URL, voaId).catch {
                Timber.d("异常")
            }.collect {
                it.onSuccess { result ->
                    ("语音发送" + if (result.isNotEmpty()) "成功" else "失败").showToast()
                }.onFailure { e ->
                    "加载失败${e.message}".showToast()
                }
            }
        }
    }

    private fun synthesisVideos() {
        val synthesisLimit = 2
        val successLength = list.filter { it.success }.size
        if (successLength < 2) {
            "至少读${synthesisLimit}句方可合成".showToast()
            return
        }
        var isCurrentMonth = true
        var itemIndex = 0
        for (i in list.indices) {
            val item = list[i]
            if (item.success) {
                val videoUrl = item.selfVideoUrl
                val start = videoUrl.indexOf("/") + 1
                val end = videoUrl.indexOf("/${AppClient.appName}")
                val result = videoUrl.substring(start, end)
                val year = result.substring(0, 4)
                val month = result.substring(4)
                val currentYear = System.currentTimeMillis().timeStampDate("yyyy")
                val currentMonth = System.currentTimeMillis().timeStampDate("MM")
                isCurrentMonth = (year == currentYear && month == currentMonth)
                if (!isCurrentMonth) {
                    itemIndex = i
                    break
                }
            }
        }
        if (!isCurrentMonth) {
            "当前合成录音中含非本月的录音数据, 第${itemIndex + 1}句;请重新录制后再进行合成".showToast()
            return
        }
        val builder = StringBuilder()
        var totalScore = 0
        list.forEach {
            if (it.success) {
                builder.append(it.selfVideoUrl + ",")
                totalScore += it.fraction.toInt()
            }
        }
        lifecycleScope.launch {
            toeflViewModel.mergeVideos(builder.toString()).catch {
                Timber.d("异常")
            }.collect {
                it.onSuccess { result ->
                    mergeResult = result
                    "合成成功".showToast()
                    bind.synthesisScore.text = (totalScore / successLength).toString()
                    bind.mergeLayout.visibility = View.VISIBLE
                }.onFailure { error ->
                    "合成失败  ${error.judgeType()}".showToast()
                }
            }
        }
        //合成之后可以试听
    }

    private val evalListener = object : OnEvaluationListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun showOperate(position: Int) {
            for (i in list.indices) {
                list[i].showOperate = ((list[i].senIndex.toInt() - 1) == position)
            }
            evalAdapter.notifyDataSetChanged()
        }

        override fun openMike(position: Int, img: ImageView) {

            //currentPosition置为-1为没有操作的对象，可以播放或者评测
            if (currentPosition != -1 && currentPosition != position) {

                return
            }
            var isPlay = false//是否有播放的
            evalAdapter.videoList.also { videoList ->

                videoList.forEach {

                    try {
                        if (it.getMediaPlayer().isPlaying) {
                            isPlay = true
                            return@also
                        }
                    } catch (_: UninitializedPropertyAccessException) {

                    }
                }
            }
            if (isPlay) {//播放的音频的时候，不录音
                return
            }
            currentRecorder = img
            currentPosition = position
            if (isRecording) {
                showActivityLoad<QuestionInfoActivity>()
                stopRecord()
                img.setBackgroundResource(R.drawable.mike_grey)
                return
            }
            val permissionArray = arrayOf(Manifest.permission.RECORD_AUDIO)
            requestPermissionLaunch.checkObtainPermission(permissionArray) { showFirstHint() }
        }

        override fun playSelf(position: Int, img: RoundProgressBar) {


            if (!player.isPlaying) {
                img.setBackgroundResource(R.drawable.pause_evaluation)
                player.apply {
                    reset()
                    val url = list[position].selfVideoUrl.changeVideoUrl()
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener {
                        img.inflateMax(duration / 1000)
                        start()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                if (currentPosition - duration > 1) {
                                    img.inflateProgress(0)
                                    img.setBackgroundResource(R.drawable.play_evaluation)
                                } else {
                                    img.inflateProgress(currentPosition)
                                }
                            }
                        }, 50, 100)
                    }
                    setOnCompletionListener { img.setBackgroundResource(R.drawable.play_evaluation) }
                }
            } else {
                "正在播放中".showToast()
            }
        }

        override fun releaseSimple(bean: TextItem) {
            lifecycleScope.launch {
                toeflViewModel.releaseSimple(bean).onStart {
                    showActivityLoad<QuestionInfoActivity>()
                }.catch {
                    it.judgeType().showToast()
                    dismissActivityLoad<QuestionInfoActivity>()
                }.collect {
                    delay(500L)
                    toeflViewModel.transferReleaseSimple()
                    "分享排行榜成功".showToast()
                    dismissActivityLoad<QuestionInfoActivity>()
                }
            }
        }

        override fun searchWord(
            word: String,
            view: SelectableTextView,
            evalSuccess: Boolean,
            bean: TextItem
        ) {
            lifecycleScope.launch {
                delay(1000)
                view.dismissSelected()
            }
            if (evalSuccess) {
                dialog.showNow(childFragmentManager, "")
                dialog.changeContent(bean, word)
            } else {
                Snackbar.make(bind.root, "是否查询$word?", Snackbar.LENGTH_LONG)
                    .setAction("查询") {
                        activity?.startShowWordActivity(word)
                        evalAdapter.videoList.forEach { it.stop() }
                    }.show()
            }
        }
    }
}