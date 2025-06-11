package com.iyuba.toelflistening.fragment.toelf

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.iyuba.module.toolbox.DensityUtil
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.BuildConfig
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.QuestionInfoActivity
import com.iyuba.toelflistening.activity.UseInstructionsActivity
import com.iyuba.toelflistening.adapter.SentenceAdapter
import com.iyuba.toelflistening.bean.AdItem
import com.iyuba.toelflistening.bean.CallPhoneEvent
import com.iyuba.toelflistening.bean.SentenceItem
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.databinding.OriginalFragmentBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.java.Constant
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.SearchWordListener
import com.iyuba.toelflistening.utils.findCurrSentence
import com.iyuba.toelflistening.utils.getAllWords
import com.iyuba.toelflistening.utils.getLocalPath
import com.iyuba.toelflistening.utils.getPercent
import com.iyuba.toelflistening.utils.getRecordTime
import com.iyuba.toelflistening.utils.isCompleted
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.judgeVip
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.spellVideoUrl
import com.iyuba.toelflistening.utils.startShowWordActivity
import com.iyuba.toelflistening.utils.view.SelectableTextView
import com.yd.saas.base.interfaces.AdViewBannerListener
import com.yd.saas.base.interfaces.AdViewInterstitialListener
import com.yd.saas.config.exception.YdError
import com.yd.saas.ydsdk.YdBanner
import com.yd.saas.ydsdk.YdInterstitial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer
import java.util.TimerTask

/**
 * 原文详情
 */
@RequiresApi(Build.VERSION_CODES.N)
class OriginalFragment : BaseFragment<OriginalFragmentBinding>(), MediaPlayer.OnPreparedListener,
    SeekBar.OnSeekBarChangeListener, SearchWordListener, MediaPlayer.OnCompletionListener,
    AdViewBannerListener {

    private lateinit var mediaPlayer: MediaPlayer
    private val timer = Timer(true)
    private val textList = mutableListOf<TextItem>()
    private val list = ArrayList<SentenceItem>()
    private val adapter = SentenceAdapter()
    private var index = 0
    private var startTime = ""

    private lateinit var adBanner: YdBanner

    private lateinit var adItem: AdItem

    private var isRequestTitle = false

    private lateinit var adHandler: Handler;

    //广告
    private lateinit var ydInterstitial: YdInterstitial

    private var interstitialAdPos = 0

    private val handler = Handler(Looper.myLooper()!!) {
        operateHandler()
        true
    }

    override fun OriginalFragmentBinding.initBinding() {

        startTime = getRecordTime()
        showActivityLoad<QuestionInfoActivity>()
        sentenceList.adapter = adapter
        nextTen.setOnClickListener(clickListener)
        control.setOnClickListener(clickListener)
        prevTen.setOnClickListener(clickListener)
        changeSpeed.setOnClickListener(clickListener)
//        bannerDefault.setOnClickListener(clickListener)
//        closeBanner.setOnClickListener(clickListener)

        control.setBackgroundResource(R.drawable.pause)
        toeflViewModel.transferQuestionItem.observe(requireActivity()) { this.question = it }
        toeflViewModel.transferTitleIntroItem.observe(requireActivity()) { this.titleIntro = it }
        toeflViewModel.transferTextList.observe(requireActivity()) { result ->
            textList.addAll(result)
            list.addAll(textList.map { SentenceItem(it.sentence, false) })
            adapter.changeData(list)
        }
        prepareStart()
        bind.videoSeek.setOnSeekBarChangeListener(this@OriginalFragment)
        adapter.wordListen = this@OriginalFragment
        listenRecordResult()
        lifecycleScope.launch {
            toeflViewModel.downError.collect {
                it.onError { e ->
                    e.judgeType().showToast()
                }
            }
        }

        lifecycleScope.launch {
            adModel.lastAdResult.collect { result ->
                result.onSuccess {

                    adItem = it

                    if (it.type.startsWith("ads")) {

                        /*  requireActivity().loadYouDao(
                              bannerDefault,
                              splashFlag = false,
                              success = { bannerLayout.visibilityState(false) },
                              error = { bannerLayout.visibilityState(true) }
                          )*/
                        dealAds(it.type)
                    } else if (it.type.equals("web")) {

                        dealWeb()
                    }
                }.onError {
//                    bannerLayout.visibilityState(true)
                }
            }
        }

        adHandler = Handler(Looper.getMainLooper(), Handler.Callback {

            isRequestTitle = false
            getBannerAd()
            adHandler.sendEmptyMessageDelayed(1, (30 * 1000).toLong())
        })
        //获取广告
        adHandler.sendEmptyMessageDelayed(1, 5 * 1000);
    }


    private fun getBannerAd() {

        GlobalHome.userInfo.isVip().let {
//            bannerLayout.visibilityState(it)
            if (!it && System.currentTimeMillis() > BuildConfig.AD_TIME) {
                adModel.requestAdType(true)
            }
        }
    }

    private fun dealWeb() {

        bind.bannerFlAd.setVisibility(View.VISIBLE)
        bind.bannerFlAd.removeAllViews()
        val bannerView: View =
            LayoutInflater.from(AppClient.context).inflate(R.layout.banner_ad, null)
        val banner_iv_pic = bannerView.findViewById<ImageView>(R.id.banner_iv_pic)
        val banner_tv_title = bannerView.findViewById<TextView>(R.id.banner_tv_title)
        val banner_iv_ad = bannerView.findViewById<ImageView>(R.id.banner_iv_ad)
        bind.bannerFlAd.addView(bannerView)

        bannerView.setOnClickListener {
            if (!adItem.startuppic_Url.isEmpty()) {

                val intent = Intent(
                    requireActivity(),
                    UseInstructionsActivity::class.java
                )
                val bundle = Bundle()
                bundle.putString("out_web_page", adItem.startuppic_Url)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        val picUrl = ("http://static3.iyuba.cn/dev/" + adItem.startuppic)
        Glide.with(AppClient.context).load(picUrl).into(banner_iv_pic)

        banner_tv_title.setText(adItem.adId)
        banner_iv_ad.setOnClickListener {
            bind.bannerFlAd.removeAllViews()
            bind.bannerFlAd.setVisibility(View.GONE)
        }
    }

    /**
     * 处理广告
     *
     * @param type
     */
    private fun dealAds(type: String) {

        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = DensityUtil.dp2px(AppClient.context, 65f)

        var adKey: String? = null
        if (type == Constant.AD_ADS2) {
            adKey = BuildConfig.BANNER_AD_KEY_CSJ
        } else if (type == Constant.AD_ADS4) {
            adKey = BuildConfig.BANNER_AD_KEY_YLH
        }
        if (adKey != null) {
            adBanner = YdBanner.Builder(requireActivity())
                .setKey(adKey)
                .setWidth(width.toFloat())
                .setHeight(height.toFloat())
                .setMaxTimeoutSeconds(5)
                .setBannerListener(this@OriginalFragment)
                .build()

            adBanner.requestBanner()

            Log.d("banner___", adKey)
        }
    }

    private fun listenRecordResult() {
        lifecycleScope.launch {
            toeflViewModel.submitListenRecord.collect {
                val descTag = "听力记录上传结果"
                it.onError { e ->
                    Timber.tag(descTag).d(e)
                }.onSuccess { result ->
                    val hint = with(result) {
                        if (this.result == "1") {
                            ("数据提交成功" + if (scores == "0") "" else "，恭喜您获得了${scores}分")
                        } else {
                            if (this.result == "0") message else "数据提交异常"
                        }
                    }
                    Timber.tag(descTag).d(hint)
                }
                delay(1000)
                startTime = getRecordTime()
            }
        }
    }


    private fun prepareStart() {
        toeflViewModel.transferMainVideo.observe(requireActivity()) {
            val videoUrl = it.spellVideoUrl()
            GlobalHome.videoUrl = videoUrl
            lifecycleScope.launch {
                flow {
                    val downFile = requireContext().getLocalPath(it)
                    val result = if (GlobalHome.userInfo.isVip()) {
                        if (downFile.exists()) {
                            val connect = URL(videoUrl).openConnection() as HttpURLConnection
                            if (downFile.length() != connect.contentLengthLong) {
                                //下载,播放在线
                                toeflViewModel.downloadVideo(Pair(videoUrl, downFile))
                                Pair(true, videoUrl)
                            } else {
                                //播放本地
                                Pair(true, downFile.absolutePath)
                            }
                        } else {
                            toeflViewModel.downloadVideo(Pair(videoUrl, downFile))
                            //下载,播放在线
                            Pair(true, videoUrl)
                        }
                    } else {
                        //播放在线
                        Pair(false, videoUrl)
                    }
                    emit(result)
                }.catch {

                    Timber.d("catch")
                }.flowOn(Dispatchers.IO)
                    .collect {
                        mediaPlayer = with(MediaPlayer()) {
                            (if (it.first) "当前为VIP高速播放" else "当前为普通播放").showToast()
                            setDataSource(it.second)
                            prepareAsync()
                            setOnPreparedListener(this@OriginalFragment)
                            setOnCompletionListener(this@OriginalFragment)
                            this
                        }
                    }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun operateHandler() {
        val current = mediaPlayer.currentPosition
        bind.videoSeek.progress = current
        bind.videoSeek.progress = current
        bind.nowTime.text = changeTime(current)
        if (list.isNotEmpty() && textList.isNotEmpty() && list.size == textList.size) {
            val nowCurrent = current / 1000
            val last = textList.size - 1


            for (i in list.indices) {

                list[i].flag = false
            }
            for (i in textList.indices) {
//                list[0].flag = nowCurrent in (textList[0].timing..textList[1].timing)
//                list[i].flag = i!=0&&i!=textList.size-1&&nowCurrent in (textList[i].timing..textList[i+1].timing)
//                list[last].flag = nowCurrent >textList[last].timing
                list[index].flag = false
                if ((i + 1) < textList.size) {

                    if (nowCurrent >= textList[i].timing && nowCurrent < textList[i + 1].timing) {

                        index = i
                        list[index].flag = true
                        break
                    }
                } else {

                    index = list.size - 1
                    list[index].flag = true
                }
            }
            /*            list.forEach { i->
                            if (i.flag) index = list.indexOfFirst { i == it }
                        }*/
            bind.sentenceList.post {
                bind.sentenceList.smoothScrollToPosition(index)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun initEventBus(): Boolean = true


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: CallPhoneEvent) {
        pause()
    }


    override fun onPrepared(p0: MediaPlayer?) {
        start()
        startTime = getRecordTime()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.sendEmptyMessage(0)
            }
        }, 0, 200)
        val duration = mediaPlayer.duration
        bind.videoSeek.max = duration
        bind.sumTime.text = changeTime(duration)
        dismissActivityLoad<QuestionInfoActivity>()
    }

    private fun start() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            bind.control.setBackgroundResource(R.drawable.pause)
        }
    }

    private fun pause() {
        if (!::mediaPlayer.isInitialized) {
            return
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            bind.control.setBackgroundResource(R.drawable.start)
        }
        //第几句
        val testNumber = textList.findCurrSentence(mediaPlayer)
        //已学习单词数
        val testWords = (mediaPlayer.getPercent() * textList.getAllWords()).toInt().toString()
        toeflViewModel.submitStudyRecord(
            startTime,
            mediaPlayer.isCompleted(),
            testWords,
            testNumber,
            textList[0].titleNum
        )
    }


    private fun changePlayCurrent(flag: Boolean) {
        val current = mediaPlayer.currentPosition
        val cursor = 1000 * 10
        if (flag) {
            mediaPlayer.seekTo(current + cursor)
        } else {
            if (current > cursor) {
                mediaPlayer.seekTo(current - 1000 * 10)
            }
        }
    }

    private val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.prev_ten -> changePlayCurrent(false)
            R.id.change_speed -> {
                if (activity is QuestionInfoActivity) {
                    val question = activity as QuestionInfoActivity
                    question.judgeVip("调速") { showWindow() }
                }
            }

            R.id.control -> {
                if (mediaPlayer.isPlaying) {
                    pause()
                } else {
                    bind.control.setBackgroundResource(R.drawable.pause)
                    mediaPlayer.start()
                }
            }

            R.id.next_ten -> changePlayCurrent(true)
//            R.id.banner_default -> requireActivity().clickSkipWeb(bind.bannerDefault)
//            R.id.close_banner -> bind.bannerLayout.visibilityState(true)
        }
    }

    private fun showWindow() {
        var position = 0
        val array = resources.getStringArray(R.array.speed)
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setSingleChoiceItems(
            array, 0
        ) { _, p1 ->
            position = p1
        }.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            changePlaySpeed(0.5f * (position + 1))
            array[position].showToast()
        }
        dialog.show()
    }

    private fun changePlaySpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = mediaPlayer.playbackParams
                params.speed = speed
                mediaPlayer.playbackParams = params
                mediaPlayer.isLooping = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mediaPlayer.stop()
        mediaPlayer.release()
        timer.cancel()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        p0?.let { mediaPlayer.seekTo(it.progress) }
    }

    override fun searchListener(word: String, view: SelectableTextView) {
        lifecycleScope.launch {
            delay(1000)
            view.dismissSelected()
        }
        Snackbar.make(bind.root, "是否查询$word?", Snackbar.LENGTH_LONG)
            .setAction("查询") {
                activity?.startShowWordActivity(word)
            }.show()
    }


    override fun onCompletion(p0: MediaPlayer?) {//完成
        bind.control.setBackgroundResource(R.drawable.start)

        requestAd()
    }


    /**
     * 请求插屏广告
     */
    private fun requestAd() {

        if (GlobalHome.userInfo.isVip()) {
            return
        }

        if (this::ydInterstitial.isInitialized) {

            ydInterstitial.destroy()
        }

        val displayMetrics = resources.displayMetrics
        val type = if (interstitialAdPos == 0) {
            BuildConfig.INTERSTITIAL_AD_KEY_CSJ
        } else if (interstitialAdPos == 1) {
            BuildConfig.INTERSTITIAL_AD_KEY_YLH
        } else {
            BuildConfig.INTERSTITIAL_AD_KEY_BD
        }

        ydInterstitial = YdInterstitial.Builder(requireActivity())
            .setKey(type)
            .setWidth((displayMetrics.widthPixels * 0.8).toInt())
            .setHeight((displayMetrics.heightPixels * 0.5).toInt())
            .setInterstitialListener(object : AdViewInterstitialListener {
                override fun onAdReady() {
                    Timber.d("onAdReady")
                    ydInterstitial.show()
                }

                override fun onAdDisplay() {
                    Timber.d("onAdDisplay")
                }

                override fun onAdClick(s: String) {
                    Timber.d("onAdClick")
                }

                override fun onAdClosed() {
                    Timber.d("onAdClosed")
                }

                override fun onAdFailed(ydError: YdError) {
                    if (interstitialAdPos == 0) {
                        interstitialAdPos = 1
                        requestAd()
                    } else if (interstitialAdPos == 1) {
                        interstitialAdPos = 2
                        requestAd()
                    } else {
                        Timber.d(ydError.msg)
                    }
                }
            })
            .build()
        ydInterstitial.requestInterstitial()
    }

    override fun onAdFailed(p0: YdError?) {

        if (!isRequestTitle) {

            isRequestTitle = true
            dealAds(adItem.title)
        }
        Log.d("banner111", "onAdFailed:" + p0.toString())
    }

    override fun onReceived(p0: View?) {

        bind.bannerFlAd.removeAllViews()
        bind.bannerFlAd.visibility = View.VISIBLE
        bind.bannerFlAd.addView(p0)


        Log.d("banner111", "onReceived")
    }

    override fun onAdExposure() {
    }

    override fun onAdClick(p0: String?) {
    }

    override fun onClosed() {

        bind.bannerFlAd.visibility = View.GONE
        Log.d("banner111", "onClosed")
    }

}